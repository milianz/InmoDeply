package org.milianz.inmomarketbackend.Domain.Entities.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDefaultDTO {
    private UUID id;
    private UUID publicationId;
    private String publicationAddress;
    private UUID reporterId;
    private String reporterName;
    private String reason;
    private String description;
    private String status;
    private LocalDateTime reportDate;
}