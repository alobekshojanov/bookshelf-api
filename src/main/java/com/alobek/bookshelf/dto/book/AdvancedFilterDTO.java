package com.alobek.bookshelf.dto.book;

import com.alobek.bookshelf.enums.BookGenre;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdvancedFilterDTO {

    private String author;
    private BookGenre genre;
    private Integer publishedYear;
    private String keyword;

}
