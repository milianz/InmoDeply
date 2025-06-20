package org.milianz.inmomarketbackend.Services;

import lombok.RequiredArgsConstructor;
import org.milianz.inmomarketbackend.Domain.Entities.Publication;
import org.milianz.inmomarketbackend.Domain.Entities.Report;
import org.milianz.inmomarketbackend.Domain.Entities.User;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.ReportDefaultDTO;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.ReportSaveDTO;
import org.milianz.inmomarketbackend.Domain.Repositories.iPublicationRepository;
import org.milianz.inmomarketbackend.Domain.Repositories.iReportRepository;
import org.milianz.inmomarketbackend.Domain.Repositories.iUserRepository;
import org.milianz.inmomarketbackend.Payload.Response.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    private static final int REPORTS_THRESHOLD = 5;

    private final iReportRepository reportRepository;
    private final iPublicationRepository publicationRepository;
    private final iUserRepository userRepository;

    @Transactional
    public ResponseEntity<?> createReport(ReportSaveDTO reportSaveDTO) {
        try {
            User currentUser = getCurrentUser();

            Publication publication = publicationRepository.findById(reportSaveDTO.getPublicationId())
                    .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));

            if (publication.getStatus() != Publication.PublicationStatus.ACTIVE) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("No se puede reportar una publicación inactiva"));
            }

            if (publication.getUser().getId().equals(currentUser.getId())) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("No puedes reportar tu propia publicación"));
            }

            if (reportRepository.existsByPublicationIdAndReporterId(
                    reportSaveDTO.getPublicationId(), currentUser.getId())) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Ya has reportado esta publicación"));
            }

            Report report = Report.builder()
                    .publication(publication)
                    .reporter(currentUser)
                    .reason(reportSaveDTO.getReason())
                    .description(reportSaveDTO.getDescription())
                    .status(Report.ReportStatus.PENDING)
                    .reportDate(LocalDateTime.now())
                    .build();

            reportRepository.save(report);

            logger.info("Report created by user {} for publication {}",
                    currentUser.getEmail(), publication.getId());

            checkReportsThreshold(publication);

            return ResponseEntity.ok(new MessageResponse("Reporte creado exitosamente"));

        } catch (Exception e) {
            logger.error("Error creating report: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al crear el reporte: " + e.getMessage()));
        }
    }

    private void checkReportsThreshold(Publication publication) {
        long reportCount = reportRepository.countByPublicationIdAndStatus(
                publication.getId(), Report.ReportStatus.PENDING);

        if (reportCount >= REPORTS_THRESHOLD) {
            // Cambiar estado de la publicación
            publication.setStatus(Publication.PublicationStatus.INACTIVE);
            publicationRepository.save(publication);

            // Marcar reportes como revisados
            reportRepository.findByPublicationIdAndStatus(
                            publication.getId(), Report.ReportStatus.PENDING)
                    .forEach(report -> {
                        report.setStatus(Report.ReportStatus.RESOLVED);
                        reportRepository.save(report);
                    });

            logger.warn("Publication {} has been deactivated due to {} reports",
                    publication.getId(), reportCount);
        }
    }

    public Page<ReportDefaultDTO> getMyReports(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<Report> reports = reportRepository.findByReporterId(currentUser.getId(), pageable);
        return reports.map(this::convertToDTO);
    }

    public Page<ReportDefaultDTO> getAllReports(Pageable pageable) {
        Page<Report> reports = reportRepository.findByStatus(Report.ReportStatus.PENDING, pageable);
        return reports.map(this::convertToDTO);
    }

    public Page<ReportDefaultDTO> getReportsByPublication(UUID publicationId, Pageable pageable) {
        Page<Report> reports = reportRepository.findByPublicationId(publicationId, pageable);
        return reports.map(this::convertToDTO);
    }

    @Transactional
    public ResponseEntity<?> resolveReport(UUID reportId, String action) {
        try {
            Report report = reportRepository.findById(reportId)
                    .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));

            switch (action.toUpperCase()) {
                case "APPROVE":
                    report.setStatus(Report.ReportStatus.RESOLVED);
                    // Podrías también desactivar la publicación aquí si es necesario
                    break;
                case "DISMISS":
                    report.setStatus(Report.ReportStatus.DISMISSED);
                    break;
                default:
                    return ResponseEntity.badRequest()
                            .body(new MessageResponse("Acción no válida"));
            }

            reportRepository.save(report);
            return ResponseEntity.ok(new MessageResponse("Reporte resuelto exitosamente"));

        } catch (Exception e) {
            logger.error("Error resolving report: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al resolver el reporte"));
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private ReportDefaultDTO convertToDTO(Report report) {
        return new ReportDefaultDTO(
                report.getId(),
                report.getPublication().getId(),
                report.getPublication().getPropertyAddress(),
                report.getReporter().getId(),
                report.getReporter().getName(),
                report.getReason(),
                report.getDescription(),
                report.getStatus().name(),
                report.getReportDate()
        );
    }
}