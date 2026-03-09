package com.alobek.bookshelf.dto.book;

import com.alobek.bookshelf.enums.BookGenre;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class BookDetailDTO {

    private Integer id;
    private String title;
    private String author;
    private String description;
    private BookGenre genre;
    private Integer publishedYear;
    private BookPhotoResponseDTO photo;
    private LocalDateTime createdDate;



}
