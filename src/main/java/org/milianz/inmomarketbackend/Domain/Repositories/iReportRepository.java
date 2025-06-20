package org.milianz.inmomarketbackend.Domain.Repositories;

import org.milianz.inmomarketbackend.Domain.Entities.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface iReportRepository extends iGenericRepository<Report, UUID> {

    boolean existsByPublicationIdAndReporterId(UUID publicationId, UUID reporterId);

    long countByPublicationIdAndStatus(UUID publicationId, Report.ReportStatus status);

    List<Report> findByPublicationIdAndStatus(UUID publicationId, Report.ReportStatus status);

    Page<Report> findByReporterId(UUID reporterId, Pageable pageable);

    // Para admin: obtener todos los reportes pendientes
    Page<Report> findByStatus(Report.ReportStatus status, Pageable pageable);

    // Obtener reportes de una publicación específica
    Page<Report> findByPublicationId(UUID publicationId, Pageable pageable);
}