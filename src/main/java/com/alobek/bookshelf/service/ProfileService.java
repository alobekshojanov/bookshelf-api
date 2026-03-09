package com.alobek.bookshelf.service;

import com.alobek.bookshelf.config.CustomUserDetails;
import com.alobek.bookshelf.config.JwtAuthenticationFilter;
import com.alobek.bookshelf.dto.AppResponse;
import com.alobek.bookshelf.dto.ConfirmationCodeDTO;
import com.alobek.bookshelf.dto.ProfileDetailUpdateDTO;
import com.alobek.bookshelf.dto.ProfilePhotoUpdateDTO;
import com.alobek.bookshelf.dto.profile.ProfilePasswordUpdateDTO;
import com.alobek.bookshelf.dto.profile.ProfilePhotoResponseDTO;
import com.alobek.bookshelf.dto.profile.ProfileResponseDTO;
import com.alobek.bookshelf.dto.profile.ProfileUsernameUpdateDTO;
import com.alobek.bookshelf.entity.ProfileEntity;
import com.alobek.bookshelf.enums.AppLanguage;
import com.alobek.bookshelf.enums.GeneralStatus;
import com.alobek.bookshelf.enums.ProfileRole;
import com.alobek.bookshelf.exps.AppBadException;
import com.alobek.bookshelf.repository.ProfileRepository;
import com.alobek.bookshelf.repository.ProfileRoleRepository;
import com.alobek.bookshelf.util.EmailUtil;
import com.alobek.bookshelf.util.JwtUtil;
import com.alobek.bookshelf.util.PageUtil;
import com.alobek.bookshelf.util.SpringSecurityUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {


    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private AttachService  attachService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private EmailSendingService emailSendingService;

    @Autowired
    private EmailHistoryService emailHistoryService;

    @Autowired
    private ProfileRoleRepository profileRoleRepository;

    @Autowired
    private ResourceBundleService bundleService;



    public AppResponse<String> updateDetail(ProfileDetailUpdateDTO dto, AppLanguage lang){
            Integer profileId= SpringSecurityUtil.getCurrentUserId();

            profileRepository.updateDetail(profileId, dto.getName());
            return new AppResponse<>(bundleService.getMessage("profile.detail.updated", lang));
    }

    public AppResponse<String> updatePhoto(String photoId, AppLanguage lang) {
        Integer profileId = SpringSecurityUtil.getCurrentUserId();
        ProfileEntity entity = getById(profileId);

        profileRepository.updatePhoto(profileId, photoId);

        if (entity.getPhotoId() != null || !entity.getPhotoId().equals(photoId)) {
            // update photo ...
            attachService.delete(entity.getPhotoId());
        }
        //profileRepository.save(entity);
         // what would be with old photo?
        return new AppResponse<>(bundleService.getMessage("profile.photo.updated", lang));
    }

    public AppResponse<String> updatePassword(ProfilePasswordUpdateDTO dto, AppLanguage lang) {
        Integer profileId = SpringSecurityUtil.getCurrentUserId();
        ProfileEntity profile = getById(profileId);
        if (!bCryptPasswordEncoder.matches(dto.getCurrentPassword(),  profile.getPassword())) {
            throw new AppBadException("current.new.password");
        }

        profile.setPassword(bCryptPasswordEncoder.encode(dto.getNewPassword()));
        profileRepository.updatePassword(profileId, bCryptPasswordEncoder.encode(dto.getNewPassword()));

        return new AppResponse<>(bundleService.getMessage("password.updated.successfully", lang));
    }

    public AppResponse<String> updateUsername(ProfileUsernameUpdateDTO dto, AppLanguage lang){
        // check given username
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
        if (optional.isPresent()) {
            throw new AppBadException(bundleService.getMessage("email.already.exists", lang ));
        }
        // send confirmation code ...
        if (!EmailUtil.isEmail(dto.getUsername())) {
            throw new AppBadException(bundleService.getMessage("invalid.username", lang));
        }

        emailSendingService.sendChangeUsernameEmail(dto.getUsername());
        // save new username
        Integer profileId = SpringSecurityUtil.getCurrentUserId();
        profileRepository.updateTempUsername(profileId, dto.getUsername());

        return new  AppResponse<>(bundleService.getMessage("username.updated", lang));
    }

    public ProfileEntity getById(Integer profileId){
        /*Optional<ProfileEntity> optional = profileRepository.findByIdAndVisibleTrue(profileId);
        if (optional.isEmpty()) {
            throw new AppBadException("Profile not found");
        }
        return optional.get();*/

        return profileRepository.findByIdAndVisibleTrue(profileId).orElseThrow( () -> {
            throw new AppBadException("Profile id not found");
        });
    }

    public AppResponse<String> updateUsernameConfirm(ConfirmationCodeDTO dto, AppLanguage lang) {
        Integer profileId = SpringSecurityUtil.getCurrentUserId();
        ProfileEntity profile = getById(profileId);
        String tempUsername = profile.getTempUsername();
        if (EmailUtil.isEmail(tempUsername)) {
            emailHistoryService.check(tempUsername, dto.getCode(), lang);
        }
        // update username ...
        profileRepository.updateUsername(profileId, tempUsername);

        List<ProfileRole> roles = profileRoleRepository.getAllRolesListByProfileId(profile.getId());
        String jwt = JwtUtil.encode(tempUsername, roles, profile.getId());

        return new AppResponse<>(jwt, bundleService.getMessage("username.updated", lang));
    }

    public AppResponse<ProfileResponseDTO> getDetail(AppLanguage lang) {

        CustomUserDetails profile = SpringSecurityUtil.getCurrentProfile();
        Integer profileId = profile.getId();

        ProfileEntity profileEntity = profileRepository.findByIdAndVisibleTrue(profileId).orElseThrow(() ->
            new AppBadException(bundleService.getMessage("book.not.found", lang)));


        return new AppResponse<>(toDTO(profileEntity), "these.profile.details");
    }

    public AppResponse<?> profileList(int page, int size, AppLanguage lang) {
        // make page request from page parameter ...

        // check whether current user is ADMIN - agar ADMIN bo‘lmasa access denied (403)
        CustomUserDetails profile = SpringSecurityUtil.getCurrentProfile();

        // create Pageable object from page and size - page va size asosida pagination tayyorlash
        int currentPage = PageUtil.page(page);
        PageRequest requestPage = PageRequest.of(currentPage, size);

        // get paginated profiles from database (visible = true) - profileRepository orqali Page<ProfileEntity> olish
        Page<ProfileEntity> profileEntityPage = profileRepository.findAll(requestPage);


        return new AppResponse<>(bundleService.getMessage("", lang));
    }

    public AppResponse<String> blockProfile(Integer profileId, AppLanguage lang) {

        CustomUserDetails currentUser = SpringSecurityUtil.getCurrentProfile();
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            throw new AppBadException(bundleService.getMessage("access.denied", lang));
        }

        // validation profile ID ...
        ProfileEntity profile = profileRepository.findByIdAndVisibleTrueAndStatus(profileId, GeneralStatus.ACTIVE).orElseThrow(() ->
                new AppBadException(bundleService.getMessage("profile.not.found", lang)));

        // check this id is not Admin's id ...
        Integer currentProfile = SpringSecurityUtil.getCurrentUserId();
        if (currentProfile.equals(profileId)){
            throw new AppBadException(bundleService.getMessage("admin.cannot.block", lang));
        }

        // change status into BLOCK ...
        profileRepository.blockStatus(profileId, GeneralStatus.BLOCK);

        // response ...
        return new AppResponse<>(profile.getUsername(), bundleService.getMessage("profile.blocked", lang));
    }

    public  AppResponse<String> unblockProfile(Integer profileId, AppLanguage lang) {
        // check for admin ...
        CustomUserDetails currentUser = SpringSecurityUtil.getCurrentProfile();
        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            throw new AppBadException(bundleService.getMessage("access.denied", lang));
        }

        // check for admin id, because he cannot unblock himself
        if (currentUser.getId().equals(profileId)) {
            throw new AppBadException(bundleService.getMessage("admin.cannot.block", lang));
        }

        // find profile by id ...
        ProfileEntity profile = profileRepository.findByIdAndVisibleTrueAndStatus(profileId, GeneralStatus.BLOCK).orElseThrow(() ->
                new AppBadException(bundleService.getMessage("profile.not.found", lang)));

        // unblock user
        profileRepository.changeStatus(profileId, GeneralStatus.ACTIVE);


        return new AppResponse<>(profile.getUsername(), bundleService.getMessage("profile.unblocked", lang));
    }


    public AppResponse<String> deleteProfile(Integer profileId, AppLanguage lang) {

        // check for admin role ....
        CustomUserDetails currentUser = SpringSecurityUtil.getCurrentProfile();
        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if(!isAdmin){
            throw new AppBadException(bundleService.getMessage("access.denied", lang));
        }

        // whether the admin id is th same with the profileId ...
        if(currentUser.getId().equals(profileId)){
            throw new AppBadException(bundleService.getMessage("admin.cannot.delete", lang));
        }

        // Find profile with ID ...
        ProfileEntity profile = profileRepository.findByIdAndVisibleTrueAndStatus(profileId, GeneralStatus.ACTIVE).orElseThrow(() ->
                new AppBadException(bundleService.getMessage("profile.not.found", lang)));

        // make soft delete ...
        profileRepository.deleteProfile(profileId, Boolean.FALSE);

        // response ...
        return new AppResponse<>(profile.getUsername(), bundleService.getMessage("profile.deleted", lang));
    }




    public ProfileResponseDTO toDTO(ProfileEntity entity) {

        if (entity == null) return null;

        String baseURL = "http://localhost:8080/attach/open/";

        ProfilePhotoResponseDTO photoDto = null;
        if (entity.getPhotoId() != null) {
            String url = baseURL + entity.getPhotoId();
            photoDto = new ProfilePhotoResponseDTO();
            photoDto.setId(entity.getPhotoId());
            photoDto.setUrl(url);
        }

        ProfileResponseDTO dto = new ProfileResponseDTO();
        dto.setName(entity.getName());
        dto.setUsername(entity.getUsername());
        dto.setPhoto(photoDto);
        dto.setRoleList(profileRoleRepository.getAllRolesByProfileId(entity.getId()));

        return dto;
    } // this for getDetail() method!!!

    @Transactional
    public AppResponse<String> updateName(String name, AppLanguage lang) {

        if (name == null || name.trim().length() < 3) {
            throw new AppBadException(bundleService.getMessage("invalid.name", lang));
        }

        // check whether user authorized ...
        CustomUserDetails currentProfile = SpringSecurityUtil.getCurrentProfile();
        Integer profileId = currentProfile.getId();

        // find the user and change name ...
        ProfileEntity profile = profileRepository.findByIdAndVisibleTrue(profileId).orElseThrow(() ->
                new AppBadException(bundleService.getMessage("profile.not.found", lang)));

        profile.setName(name);
        //profileRepository.save(profile);


        // make response ...
        return new AppResponse<>(bundleService.getMessage("profile.name.updated", lang));
    }


}
