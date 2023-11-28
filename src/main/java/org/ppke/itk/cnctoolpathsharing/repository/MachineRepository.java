package org.ppke.itk.cnctoolpathsharing.repository;

import org.ppke.itk.cnctoolpathsharing.domain.Machine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MachineRepository extends JpaRepository<Machine, Integer> {
    Machine save(Machine m);

    Page<Machine> findByNameContainingAndManufacturerContaining(String name,
                                                                String manufacturer,
                                                                Pageable page);
}
