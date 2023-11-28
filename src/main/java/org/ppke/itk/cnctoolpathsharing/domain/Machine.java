package org.ppke.itk.cnctoolpathsharing.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collections;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "machine")
public class Machine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique=true)
    private String name;

    @Column
    private String manufacturer;

    @Column
    private Double workspaceWidth;

    @Column
    private Double workspaceHeight;

    @Column
    private Double workspaceDepth;

    @Column
    private MachineType machineType;

    @Column
    private String pictureUri;

    @JsonBackReference
    @ManyToMany(mappedBy="supportedMachines",fetch=FetchType.EAGER)
    private Set<MachineTool> supportedTools = Collections.emptySet();
}