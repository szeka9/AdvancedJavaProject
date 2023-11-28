package org.ppke.itk.cnctoolpathsharing.repository;


import org.ppke.itk.cnctoolpathsharing.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    User save(User user);

    Optional<User> findByName(String name);
}
