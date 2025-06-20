package org.milianz.inmomarketbackend.Domain.Repositories;

import org.milianz.inmomarketbackend.Domain.Entities.PropertyType;
import java.util.Optional;
import java.util.UUID;

public interface iPropertyTypeRepository extends iGenericRepository<PropertyType, UUID> {
    Optional<PropertyType> findByTypeName(String typeName);
}
