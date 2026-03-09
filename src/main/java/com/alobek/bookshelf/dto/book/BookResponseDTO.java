package com.alobek.bookshelf.dto.book;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookResponseDTO {

    private Integer id;
    private String title;
    private String author;
    private BookPhotoResponseDTO photo;
    private LocalDateTime createdDate;

}
