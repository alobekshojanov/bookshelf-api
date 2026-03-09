package com.alobek.bookshelf.dto.borrow;

import com.alobek.bookshelf.enums.BorrowStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AdminBorrowDTO {

    private Integer borrowId;           // Borrow record ID
    private BookInfoDTO book;           // Nested book info (id + title)
    private ProfileInfoDTO profile;           // Nested user info (id + username / full name / email)
    private LocalDate borrowedDate;   // When book was borrowed
    private LocalDate returnedDate;   // When book was returned (null if not yet)
    private BorrowStatus status;        // BORROWED / RETURNED

}
