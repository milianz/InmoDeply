package org.milianz.inmomarketbackend.Domain.Entities.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteSaveDTO {

    @NotNull(message = "El ID de la publicación es obligatorio")
    private UUID publicationId;
}