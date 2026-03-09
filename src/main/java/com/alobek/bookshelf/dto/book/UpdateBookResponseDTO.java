package com.alobek.bookshelf.dto.book;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBookResponseDTO {

    private Integer id;
    private String title;
    private String author;
    private BookPhotoResponseDTO photo;

}
