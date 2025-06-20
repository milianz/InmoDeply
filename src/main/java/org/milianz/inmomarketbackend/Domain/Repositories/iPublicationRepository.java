package org.milianz.inmomarketbackend.Domain.Repositories;

import org.milianz.inmomarketbackend.Domain.Entities.DTOs.PublicationDefaultDTO;
import org.milianz.inmomarketbackend.Domain.Entities.Publication;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface iPublicationRepository extends iGenericRepository<Publication, UUID>{
    List<Publication> findByLocation_Department(String locationDepartment);
    List<Publication> findByPropertyPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    List<Publication> findByPropertyType_TypeName(String propertyTypeTypeName);
    List<Publication> findByPropertySizeBetween(BigDecimal minSize, BigDecimal maxSize);
    List<Publication> findByPropertyBedrooms(Integer propertyBedrooms);
    List<Publication> findByPropertyFloors(Integer propertyFloors);
    List<Publication> findByPropertyParking(Integer propertyParking);
    List<Publication> findByPropertyFurnished(Boolean propertyFurnished);
    List<Publication> findByStatus(Publication.PublicationStatus status);
    List<Publication> findByUser_Id(UUID userId);
    Optional<Publication> findById(UUID id);
    List<Publication> findTop10ByOrderByCreatedAtDesc();
}