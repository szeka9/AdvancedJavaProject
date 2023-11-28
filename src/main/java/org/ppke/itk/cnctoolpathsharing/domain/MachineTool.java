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
@Table(name = "machinetool")
public class MachineTool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique=true)
    private String name;

    @ManyToMany(fetch=FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(name="machine_machinetool",
            joinColumns = @JoinColumn(name="machinetool_id"),
            inverseJoinColumns = @JoinColumn(name="machine_id")
    )
    private Set<Machine> supportedMachines = Collections.emptySet();

    @Column
    private Set<String> supportedMaterials = Collections.emptySet();

    @JsonBackReference
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User createdByUser;
}
