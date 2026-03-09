package com.alobek.bookshelf.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfilePasswordUpdateDTO {

    @NotBlank(message = "current password required")
    private String currentPassword;

    @NotBlank(message = "new password required")
    private String newPassword;
}
