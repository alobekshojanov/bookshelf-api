package com.alobek.bookshelf.service;

import com.alobek.bookshelf.dto.AppResponse;
import com.alobek.bookshelf.dto.AuthDTO;
import com.alobek.bookshelf.dto.ProfileDTO;
import com.alobek.bookshelf.dto.RegistrationDTO;
import com.alobek.bookshelf.dto.auth.ResetPasswordConfirmDTO;
import com.alobek.bookshelf.dto.auth.ResetPasswordDTO;
import com.alobek.bookshelf.entity.ProfileEntity;
import com.alobek.bookshelf.enums.AppLanguage;
import com.alobek.bookshelf.enums.GeneralStatus;
import com.alobek.bookshelf.enums.ProfileRole;
import com.alobek.bookshelf.exps.AppBadException;
import com.alobek.bookshelf.repository.ProfileRepository;
import com.alobek.bookshelf.repository.ProfileRoleRepository;
import com.alobek.bookshelf.util.EmailUtil;
import com.alobek.bookshelf.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private ProfileRoleRepository profileRoleRepository;

    @Autowired
    private ProfileRoleService profileRoleService;

    @Autowired
    private EmailSendingService emailSendingService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private AttachService attachService;

    @Autowired
    private EmailHistoryService emailHistoryService;

    @Autowired
    private ResourceBundleService bundleService;

    public AppResponse<String> registration(RegistrationDTO dto, AppLanguage lang){
        // 1. Validation ...
        // 2. Check whether username is there ...
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
        if (optional.isPresent()) {

            ProfileEntity profile = optional.get();
            if (profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
                profileRoleService.deleteRoles(profile.getId());
                profileRepository.delete(profile);
                // send  sms|email.
            } else {
                log.warn("Profile already exists with name: {}", dto.getUsername());
                throw new AppBadException(bundleService.getMessage("email.already.exists", lang));
            }
        }
        // create user ...
        ProfileEntity entity = new ProfileEntity();
        entity.setName(dto.getName());
        entity.setUsername(dto.getUsername());
        entity.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        entity.setStatus(GeneralStatus.IN_REGISTRATION);
        entity.setVisible(true);
        entity.setCreatedDate(LocalDateTime.now());
        profileRepository.save(entity); // save to DB ...
        // Giving role to new user ...
        profileRoleService.create(entity.getId(), ProfileRole.ROLE_USER);
        // 3. Email sending ...
        emailSendingService.sendRegistrationEmail(dto.getUsername(), entity.getId(), lang);

        return new AppResponse<>(bundleService.getMessage("registration.code.sent", lang));
    }

    public AppResponse<String> regVerification(String token, AppLanguage lang) {
        try{
            Integer profileId = JwtUtil.decodeRegVerToken(token);
            ProfileEntity profile = profileService.getById(profileId);
            if (profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
                // ACTIVE
                profileRepository.changeStatus(profileId, GeneralStatus.ACTIVE);

                return new AppResponse<>(bundleService.getMessage("verification.successfully.finished", lang));
            }

        }catch (JwtException e){
        }
        log.warn("Registration email verification failed: {}", token);
        throw new AppBadException(bundleService.getMessage("verification.failed", lang));
    }

    public AppResponse<String> resetPassword(ResetPasswordDTO dto, AppLanguage lang) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrueAndStatus(dto.getUsername(),  GeneralStatus.ACTIVE);
        if (optional.isEmpty()) {

            throw new AppBadException(bundleService.getMessage("username.not.found", lang));
        }

        ProfileEntity profile = optional.get();
        if (!profile.getStatus().equals(GeneralStatus.ACTIVE)) {
            throw new AppBadException(bundleService.getMessage("username.status.not.allowed", lang));
        }

        // send reset password ...
        emailSendingService.sendResetPasswordEmail(dto.getUsername());

        return new AppResponse<>(bundleService.getMessage("confirmation.code.sent.username", lang));
    }

    public AppResponse<String> resetPasswordConfirm(ResetPasswordConfirmDTO dto, AppLanguage lang) {

        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrueAndStatus(dto.getUsername(),  GeneralStatus.ACTIVE);
        if (optional.isEmpty()) {
            throw new AppBadException(bundleService.getMessage("Username.not.exists", lang));
        }

        ProfileEntity profile = optional.get();
        if (!profile.getStatus().equals(GeneralStatus.ACTIVE)) {
            throw new AppBadException(bundleService.getMessage("username.status.not.allowed", lang));
        }

        // check code ...
        emailHistoryService.check(dto.getUsername(), dto.getConfirmCode(), lang);

        // change password ...
        profileRepository.updatePassword(profile.getId(), bCryptPasswordEncoder.encode(dto.getPassword()));

        return new  AppResponse<>(bundleService.getMessage("password.reset.done", lang));
    }

    public ProfileDTO login(AuthDTO dto, AppLanguage lang){
        Optional<ProfileEntity> optional = profileRepository.
                findByUsernameAndVisibleTrueAndStatus(dto.getUsername(),
                GeneralStatus.ACTIVE );
        if (optional.isEmpty()) {
            log.warn("Username or password wrong: {}", dto.getUsername());
            throw new AppBadException(bundleService.getMessage("username.password.incorrect", lang));
        }

        ProfileEntity profile = optional.get();
        if (!bCryptPasswordEncoder.matches(dto.getPassword(), profile.getPassword())) {
            throw new AppBadException(bundleService.getMessage("username.password.incorrect", lang));
        }

        if (!profile.getStatus().equals(GeneralStatus.ACTIVE)) {
            log.warn("Wrong status: username: {}", dto.getUsername());
            throw new AppBadException(bundleService.getMessage("username.password.incorrect", lang));
        }
        // response ...
        ProfileDTO response = new ProfileDTO();
        response.setName(profile.getName());
        response.setUsername(profile.getUsername());
        response.setRoleList(profileRoleRepository.getAllRolesByProfileId(profile.getId()));
        response.setJwt(JwtUtil.encode(profile.getUsername(), response.getRoleList(), profile.getId())); // jwt set

        return response;
    }

    public ProfileDTO getLogInResponse(ProfileEntity profile){

        ProfileDTO response = new ProfileDTO();
        response.setName(profile.getName());
        response.setUsername(profile.getUsername());
        response.setRoleList(profileRoleRepository.getAllRolesByProfileId(profile.getId()));
        response.setJwt(JwtUtil.encode(profile.getUsername(), response.getRoleList(), profile.getId()));
        response.setPhoto(attachService.attachDTO(profile.getPhotoId()));

        return response;
    }


}
