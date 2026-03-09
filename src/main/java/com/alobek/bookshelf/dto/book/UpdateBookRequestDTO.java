package com.alobek.bookshelf.dto.book;


import com.alobek.bookshelf.enums.BookGenre;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBookRequestDTO {

    private String title;
    private String author;
    private String description;
    private BookGenre genre;
    private BookPhotoResponseDTO photo;

}
