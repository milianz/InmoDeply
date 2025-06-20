package org.milianz.inmomarketbackend.Domain.Repositories;

import org.milianz.inmomarketbackend.Domain.Entities.User;
import java.util.Optional;
import java.util.UUID;

public interface iUserRepository extends iGenericRepository<User, UUID> {

    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
}
