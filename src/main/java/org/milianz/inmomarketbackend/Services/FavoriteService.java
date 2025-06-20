package org.milianz.inmomarketbackend.Services;

import lombok.RequiredArgsConstructor;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.FavoriteDefaultDTO;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.FavoriteSaveDTO;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.PublicationDefaultDTO;
import org.milianz.inmomarketbackend.Domain.Entities.Favorite;
import org.milianz.inmomarketbackend.Domain.Entities.Publication;
import org.milianz.inmomarketbackend.Domain.Entities.User;


import org.milianz.inmomarketbackend.Domain.Repositories.iFavoriteRepository;
import org.milianz.inmomarketbackend.Domain.Repositories.iPublicationRepository;
import org.milianz.inmomarketbackend.Domain.Repositories.iUserRepository;
import org.milianz.inmomarketbackend.Payload.Response.MessageResponse;
import org.milianz.inmomarketbackend.Utils.PublicationsConstructor;
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
import java.util.*;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private static final Logger logger = LoggerFactory.getLogger(FavoriteService.class);

    private final iFavoriteRepository favoriteRepository;
    private final iPublicationRepository publicationRepository;
    private final iUserRepository userRepository;

    @Transactional
    public ResponseEntity<?> toggleFavorite(FavoriteSaveDTO favoriteSaveDTO) {
        try {
            User currentUser = getCurrentUser();

            // Verificar que la publicación existe
            Publication publication = publicationRepository.findById(favoriteSaveDTO.getPublicationId())
                    .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));

            // Verificar que la publicación esté activa
            if (publication.getStatus() != Publication.PublicationStatus.ACTIVE) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("No se puede marcar como favorita una publicación inactiva"));
            }

            // Verificar que el usuario no esté marcando su propia publicación
            if (publication.getUser().getId().equals(currentUser.getId())) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("No puedes marcar como favorita tu propia publicación"));
            }

            // Verificar si ya está en favoritos
            boolean isFavorite = favoriteRepository.existsByUserIdAndPublicationId(
                    currentUser.getId(), favoriteSaveDTO.getPublicationId());

            if (isFavorite) {
                // Si ya está en favoritos, lo quitamos
                favoriteRepository.deleteByUserIdAndPublicationId(
                        currentUser.getId(), favoriteSaveDTO.getPublicationId());

                logger.info("User {} removed publication {} from favorites",
                        currentUser.getEmail(), publication.getId());

                return ResponseEntity.ok(new MessageResponse("Publicación quitada de favoritos"));

            } else {
                // Si no está en favoritos, lo agregamos
                Favorite.FavoriteId favoriteId = new Favorite.FavoriteId(
                        currentUser.getId(), favoriteSaveDTO.getPublicationId());

                Favorite favorite = Favorite.builder()
                        .id(favoriteId)
                        .user(currentUser)
                        .publication(publication)
                        .savedAt(LocalDateTime.now())
                        .build();

                favoriteRepository.save(favorite);

                logger.info("User {} added publication {} to favorites",
                        currentUser.getEmail(), publication.getId());

                return ResponseEntity.ok(new MessageResponse("Publicación agregada a favoritos"));
            }

        } catch (Exception e) {
            logger.error("Error toggling favorite: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al procesar favorito: " + e.getMessage()));
        }
    }

    public Page<FavoriteDefaultDTO> getMyFavorites(Pageable pageable) {
        User currentUser = getCurrentUser();

        // Obtener solo favoritos de publicaciones activas
        Page<Favorite> favorites = favoriteRepository.findByUserIdAndPublication_StatusOrderBySavedAtDesc(
                currentUser.getId(),
                Publication.PublicationStatus.ACTIVE,
                pageable);

        return favorites.map(this::convertToFavoriteDTO);
    }

    public ResponseEntity<?> checkIsFavorite(UUID publicationId) {
        try {
            User currentUser = getCurrentUser();

            boolean isFavorite = favoriteRepository.existsByUserIdAndPublicationId(
                    currentUser.getId(), publicationId);

            return ResponseEntity.ok(Map.of("isFavorite", isFavorite));

        } catch (Exception e) {
            logger.error("Error checking favorite status: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al verificar estado de favorito"));
        }
    }

    public ResponseEntity<?> getFavoriteStats() {
        try {
            User currentUser = getCurrentUser();

            long totalFavorites = favoriteRepository.countByUserId(currentUser.getId());

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalFavorites", totalFavorites);

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            logger.error("Error getting favorite stats: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al obtener estadísticas"));
        }
    }

    @Transactional
    public ResponseEntity<?> removeFavorite(UUID publicationId) {
        try {
            User currentUser = getCurrentUser();

            boolean exists = favoriteRepository.existsByUserIdAndPublicationId(
                    currentUser.getId(), publicationId);

            if (!exists) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("La publicación no está en favoritos"));
            }

            favoriteRepository.deleteByUserIdAndPublicationId(
                    currentUser.getId(), publicationId);

            logger.info("User {} removed publication {} from favorites",
                    currentUser.getEmail(), publicationId);

            return ResponseEntity.ok(new MessageResponse("Publicación quitada de favoritos"));

        } catch (Exception e) {
            logger.error("Error removing favorite: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al quitar de favoritos"));
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private FavoriteDefaultDTO convertToFavoriteDTO(Favorite favorite) {
        Publication publication = favorite.getPublication();

        // Reutilizar PublicationsConstructor para mantener consistencia
        PublicationsConstructor constructor = new PublicationsConstructor();
        PublicationDefaultDTO publicationDTO = constructor.PublicationUnique(publication);

        // Convertir a FavoriteDefaultDTO
        FavoriteDefaultDTO favoriteDTO = new FavoriteDefaultDTO();
        favoriteDTO.setPublicationId(publicationDTO.getId());
        favoriteDTO.setPropertyAddress(publicationDTO.getPropertyAddress());
        favoriteDTO.setTypeName(publicationDTO.getTypeName());
        favoriteDTO.setNeighborhood(publicationDTO.getNeighborhood());
        favoriteDTO.setMunicipality(publicationDTO.getMunicipality());
        favoriteDTO.setDepartment(publicationDTO.getDepartment());
        favoriteDTO.setLongitude(publicationDTO.getLongitude());
        favoriteDTO.setLatitude(publicationDTO.getLatitude());
        favoriteDTO.setPropertySize(publicationDTO.getPropertySize());
        favoriteDTO.setPropertyBedrooms(publicationDTO.getPropertyBedrooms());
        favoriteDTO.setPropertyFloors(publicationDTO.getPropertyFloors());
        favoriteDTO.setPropertyParking(publicationDTO.getPropertyParking());
        favoriteDTO.setPropertyFurnished(publicationDTO.getPropertyFurnished());
        favoriteDTO.setPropertyDescription(publicationDTO.getPropertyDescription());
        favoriteDTO.setPropertyPrice(publicationDTO.getPropertyPrice());
        favoriteDTO.setPropertyImageUrls(publicationDTO.getPropertyImageUrls());
        favoriteDTO.setAvailableTimes(publicationDTO.getAvailableTimes());
        favoriteDTO.setOwnerName(publicationDTO.getUserName());
        favoriteDTO.setSavedAt(favorite.getSavedAt());
        favoriteDTO.setIsFavorite(true); // Siempre true en esta lista

        return favoriteDTO;
    }
}