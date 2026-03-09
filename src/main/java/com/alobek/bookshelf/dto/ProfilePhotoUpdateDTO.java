package com.alobek.bookshelf.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfilePhotoUpdateDTO {

    @NotBlank(message = "photo id required")
    private String photoId;


}
