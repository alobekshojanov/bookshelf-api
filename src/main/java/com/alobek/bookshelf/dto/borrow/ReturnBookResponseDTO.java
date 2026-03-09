package com.alobek.bookshelf.dto.borrow;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReturnBookResponseDTO {

    private Integer borrowId;
    private String bookTitle;
    private LocalDate dueDate;
    private LocalDate returnDate;

}
