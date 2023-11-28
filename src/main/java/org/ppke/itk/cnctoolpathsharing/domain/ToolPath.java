package org.ppke.itk.cnctoolpathsharing.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "toolpath")
public class ToolPath {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @JsonBackReference
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User createdByUser;

    @Column(nullable = false)
    private String fileUri;

    @Column(nullable = false)
    private Boolean isPublic = false;

    @Column(nullable = false)
    private LocalDateTime dateOfCreation;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "group_id")
    private UserGroup visibleByGroup;
}