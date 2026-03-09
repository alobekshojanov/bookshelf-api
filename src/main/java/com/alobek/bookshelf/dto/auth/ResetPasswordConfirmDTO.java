package com.alobek.bookshelf.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordConfirmDTO {

    @NotBlank(message = "username required")
    private String username;

    @NotBlank(message = "Confirmation code required")
    private String confirmCode;

    @NotBlank(message = "password required")
    private String password;


}
