package com.alobek.bookshelf.config;

import com.alobek.bookshelf.entity.ProfileEntity;
import com.alobek.bookshelf.enums.ProfileRole;
import com.alobek.bookshelf.repository.ProfileRepository;
import com.alobek.bookshelf.repository.ProfileRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileRoleRepository profileRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername: " + username);

        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(username);
        if (optional.isEmpty()) {
            throw new UsernameNotFoundException("Username not found");
        }
        ProfileEntity profile = optional.get();
        List<ProfileRole> roleList =  profileRoleRepository.getAllRolesByProfileId(profile.getId());
        return new CustomUserDetails(profile, roleList);
    }
}
