package com.alobek.bookshelf.dto.profile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileListResponseDTO {

    private Integer profileId;
    private String profileName;
    private String username;
    private ProfilePhotoResponseDTO photo;
    private Integer borrowCount;

}
