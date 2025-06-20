package org.milianz.inmomarketbackend.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.FavoriteDefaultDTO;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.FavoriteSaveDTO;
import org.milianz.inmomarketbackend.Services.FavoriteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/toggle")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> toggleFavorite(@Valid @RequestBody FavoriteSaveDTO favoriteSaveDTO) {
        return favoriteService.toggleFavorite(favoriteSaveDTO);
    }

    @GetMapping("/my-favorites")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<FavoriteDefaultDTO>> getMyFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("savedAt").descending());
        Page<FavoriteDefaultDTO> favorites = favoriteService.getMyFavorites(pageable);
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/check/{publicationId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> checkIsFavorite(@PathVariable UUID publicationId) {
        return favoriteService.checkIsFavorite(publicationId);
    }

    @DeleteMapping("/remove/{publicationId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> removeFavorite(@PathVariable UUID publicationId) {
        return favoriteService.removeFavorite(publicationId);
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getFavoriteStats() {
        return favoriteService.getFavoriteStats();
    }
}