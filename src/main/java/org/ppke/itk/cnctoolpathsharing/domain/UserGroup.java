package org.ppke.itk.cnctoolpathsharing.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "toolpathsharing_group")
public class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique=true)
    private String name;

    @JsonBackReference
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "managing_user_id")
    private User managedByUser;

    @ManyToMany(fetch=FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(name="toolpathsharing_usergroup_user",
               joinColumns = @JoinColumn(name="group_id"),
               inverseJoinColumns = @JoinColumn(name="user_id")
    )

    @JsonIgnore
    @Column
    private Set<User> groupMembers;

    @JsonIgnore
    @Column
    @OneToMany(fetch=FetchType.EAGER, cascade = {CascadeType.MERGE}, mappedBy = "visibleByGroup")
    private Set<ToolPath> toolPaths;

    public void addMember(User user) {
        groupMembers.add(user);
    }

    public void removeMember(User user) {
        groupMembers.remove(user);
    }
}
