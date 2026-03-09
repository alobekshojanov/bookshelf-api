package com.alobek.bookshelf.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateDTO {

    @NotBlank(message = "name required")
    private String name;
    //private String lastName;

}
