package com.alobek.bookshelf.dto.profile;

import com.alobek.bookshelf.enums.ProfileRole;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProfileResponseDTO {

    private String name;
    private String username;
    private List<ProfileRole> roleList;
    private ProfilePhotoResponseDTO photo;

}
