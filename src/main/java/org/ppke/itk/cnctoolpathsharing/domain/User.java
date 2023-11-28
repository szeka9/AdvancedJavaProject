package org.ppke.itk.cnctoolpathsharing.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "toolpathsharing_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false, unique=true)
    private String name;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, unique=true)
    private String emailAddress;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private byte[] password;

    @JsonManagedReference
    @OneToMany(fetch=FetchType.EAGER, cascade = {CascadeType.MERGE}, mappedBy = "managedByUser")
    private Set<UserGroup> managedGroups;

    @JsonManagedReference
    @OneToMany(fetch=FetchType.EAGER, cascade = {CascadeType.MERGE}, mappedBy = "createdByUser")
    private Set<MachineTool> machineTools;

    @JsonManagedReference
    @OneToMany(fetch=FetchType.EAGER, cascade = {CascadeType.MERGE}, mappedBy = "createdByUser")
    private Set<ToolPath> toolPaths;

    @JsonBackReference
    @ManyToMany(fetch=FetchType.EAGER, mappedBy = "groupMembers")
    private Set<UserGroup> membership;
}
