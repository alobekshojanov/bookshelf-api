package com.alobek.bookshelf.entity;

import com.alobek.bookshelf.enums.GeneralStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "profile")
public class ProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "username")
    private String username; // phone or email ...

    @Column(name = "temp_username")
    private String tempUsername;

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private GeneralStatus status; // ACTIVE, BLOCK

    @Column(name = "visible")
    private Boolean visible = Boolean.TRUE;

     // role USER ADMIN

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "photo_id")
    private String photoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id",  insertable = false, updatable = false)
    private AttachEntity photo;

    /*@OneToMany(mappedBy = "profile")
    private List<ProfileRoleEntity>  roleList;*/



}
