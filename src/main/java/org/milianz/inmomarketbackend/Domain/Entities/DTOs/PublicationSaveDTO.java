package org.milianz.inmomarketbackend.Domain.Entities.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PublicationSaveDTO {
    private String propertyAddress;
    private String typeName;
    private String neighborhood;
    private String municipality;
    private String department;
    private String propertyTitle;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private BigDecimal propertySize;
    private Integer propertyBedrooms;
    private Integer propertyFloors;
    private Integer propertyParking;
    private Boolean propertyFurnished;
    private String PropertyDescription;
    private BigDecimal PropertyPrice;
    private List<AvailableTimesSaveDTO> availableTimes;
}
