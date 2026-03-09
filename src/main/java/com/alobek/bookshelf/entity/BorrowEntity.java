package com.alobek.bookshelf.entity;


import com.alobek.bookshelf.enums.BorrowStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "borrow")
public class BorrowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "profile_id")
    private Integer profileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    private ProfileEntity profile;

    @Column(name = "book_id")
    private Integer bookId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", insertable = false, updatable = false)
    private BookEntity book;

    @Column(name = "borrowed_date")
    private LocalDate borrowedDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "returned_date")
    private LocalDate returnedDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BorrowStatus status;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

}
