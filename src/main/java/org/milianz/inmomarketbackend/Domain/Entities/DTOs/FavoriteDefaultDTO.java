package org.milianz.inmomarketbackend.Domain.Entities.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteDefaultDTO {
    private UUID publicationId;
    private String propertyAddress;
    private String typeName;
    private String neighborhood;
    private String municipality;
    private String department;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private BigDecimal propertySize;
    private Integer propertyBedrooms;
    private Integer propertyFloors;
    private Integer propertyParking;
    private Boolean propertyFurnished;
    private String propertyDescription;
    private BigDecimal propertyPrice;
    private List<String> propertyImageUrls;
    private List<AvailableTimeDefaultDTO> availableTimes;
    private String ownerName;
    private LocalDateTime savedAt;
    private Boolean isFavorite; // Para indicar que está en favoritos
}