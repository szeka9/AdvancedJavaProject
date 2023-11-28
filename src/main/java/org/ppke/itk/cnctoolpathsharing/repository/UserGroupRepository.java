package org.ppke.itk.cnctoolpathsharing.repository;


import org.ppke.itk.cnctoolpathsharing.domain.UserGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupRepository extends JpaRepository<UserGroup, Integer> {
    UserGroup save(UserGroup userGroup);

    Page<UserGroup> findByNameContainingIgnoreCase(String name, Pageable page);
}
