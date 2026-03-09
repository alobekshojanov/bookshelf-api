package com.alobek.bookshelf.dto.borrow;

import com.alobek.bookshelf.entity.BookEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BorrowHistoryDTO {

    private BookInfoDTO book;
    private LocalDate borrowedDate;
    private LocalDate returnedDate;

}
