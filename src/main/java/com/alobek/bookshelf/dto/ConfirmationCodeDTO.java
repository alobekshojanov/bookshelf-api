package com.alobek.bookshelf.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmationCodeDTO {

    @NotBlank(message = "code required")
    private String code;
}
