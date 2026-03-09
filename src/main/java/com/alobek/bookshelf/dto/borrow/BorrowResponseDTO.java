package com.alobek.bookshelf.dto.borrow;

import com.alobek.bookshelf.enums.BorrowStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BorrowResponseDTO {

    private Integer borrowId;
    private Integer bookId;
    private String bookTitle;
    private LocalDate borrowedDate;
    private LocalDate dueDate;
    private BorrowStatus borrowStatus;

}
