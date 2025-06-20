package org.milianz.inmomarketbackend.Domain.Repositories;

import org.milianz.inmomarketbackend.Domain.Entities.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface iFavoriteRepository extends iGenericRepository<Favorite, Favorite.FavoriteId> {

    boolean existsByUserIdAndPublicationId(UUID userId, UUID publicationId);

    Optional<Favorite> findByUserIdAndPublicationId(UUID userId, UUID publicationId);

    Page<Favorite> findByUserIdOrderBySavedAtDesc(UUID userId, Pageable pageable);

    List<Favorite> findByUserIdOrderBySavedAtDesc(UUID userId);

    long countByUserId(UUID userId);

    long countByPublicationId(UUID publicationId);

    void deleteByUserIdAndPublicationId(UUID userId, UUID publicationId);

    Page<Favorite> findByUserIdAndPublication_StatusOrderBySavedAtDesc(
            UUID userId,
            org.milianz.inmomarketbackend.Domain.Entities.Publication.PublicationStatus status,
            Pageable pageable);

    List<Favorite> findTop10ByOrderByPublicationIdDesc();
}