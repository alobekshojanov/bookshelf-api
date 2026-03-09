package com.alobek.bookshelf.dto;

import com.alobek.bookshelf.enums.ProfileRole;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProfileDTO {

    private String name;
    private String username;
    private List<ProfileRole> roleList;
    private String jwt;
    private AttachDTO photo;
}
