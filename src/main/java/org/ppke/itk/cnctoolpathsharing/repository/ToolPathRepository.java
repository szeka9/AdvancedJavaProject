package org.ppke.itk.cnctoolpathsharing.repository;


import org.ppke.itk.cnctoolpathsharing.domain.ToolPath;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ToolPathRepository extends JpaRepository<ToolPath, Integer> {
    ToolPath save(ToolPath toolPath);

    Page<ToolPath> findByDateOfCreationBetweenAndNameContaining(LocalDateTime startDate,
                                                                LocalDateTime endDate,
                                                                String name,
                                                                Pageable page);
}
