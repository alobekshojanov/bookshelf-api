package com.alobek.bookshelf.dto.book;

import com.alobek.bookshelf.enums.BookGenre;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookDTO {

    @NotBlank(message = "title required")
    private String title;
    @NotBlank(message = "Author's name required")
    private String author;
    @NotBlank(message = "description required")
    //@Size(min = 300, message = "Description must be at least 300 characters")
    private String description;
    @NotNull(message = "genre required")
    private BookGenre genre;
    @NotNull(message = "published year required")
    private Integer publishedYear;
    @NotBlank(message = "Photo id required")
    private String photoId;

    @NotNull(message = "published year required")
    @Min(value = 1, message = "quantity must be greater than 0")
    private Integer quantity;

}
