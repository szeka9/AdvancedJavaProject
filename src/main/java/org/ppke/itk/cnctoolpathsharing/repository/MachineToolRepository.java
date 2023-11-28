package org.ppke.itk.cnctoolpathsharing.repository;

import org.ppke.itk.cnctoolpathsharing.domain.MachineTool;
import org.ppke.itk.cnctoolpathsharing.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MachineToolRepository extends JpaRepository<MachineTool, Integer> {
    MachineTool save(MachineTool t);

    List<MachineTool> findAllByCreatedByUser(User user);

    List<MachineTool> findByNameContaining(String name);
}
