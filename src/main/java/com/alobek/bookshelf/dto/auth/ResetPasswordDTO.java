package com.alobek.bookshelf.dto.auth;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordDTO {

    @NotBlank(message = "username required")
    private String username;

}
