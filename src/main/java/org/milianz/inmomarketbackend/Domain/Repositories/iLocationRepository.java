package org.milianz.inmomarketbackend.Domain.Repositories;

import jakarta.validation.constraints.NotNull;
import org.milianz.inmomarketbackend.Domain.Entities.Location;
import java.util.Optional;
import java.util.UUID;

public interface iLocationRepository extends iGenericRepository<Location, UUID> {
    Optional<Location> findByNeighborhoodAndMunicipalityAndDepartment(
            @NotNull String neighborhood,
            @NotNull String municipality,
            @NotNull String department
    );
}
