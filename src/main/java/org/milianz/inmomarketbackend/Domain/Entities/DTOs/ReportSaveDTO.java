package org.milianz.inmomarketbackend.Domain.Entities.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportSaveDTO {

    @NotNull(message = "El ID de la publicación es obligatorio")
    private UUID publicationId;

    @NotBlank(message = "El motivo del reporte es obligatorio")
    @Size(min = 3, max = 100, message = "El motivo debe tener entre 3 y 100 caracteres")
    private String reason;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;
}