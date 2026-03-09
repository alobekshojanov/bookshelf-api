package com.alobek.bookshelf.entity;


import com.alobek.bookshelf.enums.BookGenre;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "book")
@Getter
@Setter
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "genre")
    @Enumerated(EnumType.STRING)
    private BookGenre genre;

    @Column(name = "published_year")
    private Integer publishedYear;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "active")
    private Boolean active = Boolean.TRUE;

    @Column(name = "photo_id")
    private String photoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", insertable = false, updatable = false)
    private AttachEntity photo;

    @Column(name = "quantity")
    private Integer quantity;  // total copies available

    /*@Column(name = "availability")
    private Boolean availability = Boolean.TRUE;*/


}
