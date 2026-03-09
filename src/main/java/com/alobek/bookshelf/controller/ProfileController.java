package com.alobek.bookshelf.controller;


import com.alobek.bookshelf.dto.AppResponse;
import com.alobek.bookshelf.dto.ConfirmationCodeDTO;
import com.alobek.bookshelf.dto.ProfileDetailUpdateDTO;
import com.alobek.bookshelf.dto.ProfilePhotoUpdateDTO;
import com.alobek.bookshelf.dto.profile.ProfilePasswordUpdateDTO;
import com.alobek.bookshelf.dto.profile.ProfileResponseDTO;
import com.alobek.bookshelf.dto.profile.ProfileUsernameUpdateDTO;
import com.alobek.bookshelf.enums.AppLanguage;
import com.alobek.bookshelf.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@Tag(name = "Profile Controller", description = "Controller for working on profiles")
@Slf4j
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @PutMapping("/detail")
    @Operation(summary = "Update profile name", description = "API using for update name")
    public ResponseEntity<AppResponse<String>> detail(@Valid @RequestBody ProfileDetailUpdateDTO dto,
                                                      @RequestHeader(value = "Accept-Language", defaultValue = "EN") AppLanguage lang){
        return ResponseEntity.ok(profileService.updateDetail(dto, lang));
    }

    @PutMapping("/password")
    @Operation(summary = "Update password", description = "Endpoint using for updating profile's password")
    public ResponseEntity<AppResponse<String>> updatePassword(@Valid @RequestBody ProfilePasswordUpdateDTO dto,
                                                              @RequestHeader(value = "Accept-Language", defaultValue = "EN") AppLanguage lang){
        return ResponseEntity.ok(profileService.updatePassword(dto, lang));
    }

    @PutMapping("/photo")
    @Operation(summary = "Update profile's photo", description = "API using for Updating profile's photo")
    public ResponseEntity<AppResponse<String>> updatePhoto(@Valid @RequestBody ProfilePhotoUpdateDTO dto,
                                                           @RequestHeader(value = "Accept-Language", defaultValue = "EN") AppLanguage lang){
        return ResponseEntity.ok(profileService.updatePhoto(dto.getPhotoId(), lang));
    }

    @PutMapping("/username")
    @Operation(summary = "Update profile's username", description = "API using for Updating profile's username")
    public ResponseEntity<AppResponse<String>> updateUsername(@Valid @RequestBody ProfileUsernameUpdateDTO dto,
                                                              @RequestHeader(value = "Accept-Language", defaultValue = "EN") AppLanguage lang){
        return ResponseEntity.ok(profileService.updateUsername(dto, lang));
    }

    @PutMapping("/username/confirm")
    public ResponseEntity<AppResponse<String>> updateUsernameConfirm(@Valid @RequestBody ConfirmationCodeDTO dto,
                                                                     @RequestHeader(value = "Accept-Language", defaultValue = "EN")AppLanguage lang){
        return ResponseEntity.ok(profileService.updateUsernameConfirm(dto, lang));
    }

    @GetMapping("/detail")
    @Operation(summary = "Getting profile details", description = "API for getting detail")
    public ResponseEntity<AppResponse<ProfileResponseDTO>> getDetail(@RequestHeader(value = "Accept-Language", defaultValue = "EN") AppLanguage lang){
        return ResponseEntity.ok(profileService.getDetail(lang));
    }

    @PutMapping("/update-name/{name}")
    public ResponseEntity<?> updateName(@PathVariable("name") String name,
                                        @RequestHeader(value = "Accept-Language", defaultValue = "EN") AppLanguage lang){

        return ResponseEntity.ok(profileService.updateName(name, lang));
    }

    @GetMapping("/list-of-users")
    @Operation(summary = "List of users", description = "List of user based on their ....!")
    public ResponseEntity<AppResponse<?>> listOfUsers(@RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang,
                                        @RequestParam(value = "page", defaultValue = "1") int page,
                                        @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(profileService.profileList(page, size, lang));
    }


    //@PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/profile/{profileId}/block")
    @Operation(summary = "Block users", description = "API using for blocking")
    public ResponseEntity<AppResponse<String>> blockProfile(@RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang,
                                                            @PathVariable("profileId") Integer profileId ){
        return ResponseEntity.ok(profileService.blockProfile(profileId, lang));
    }

    @PutMapping("/admin/profile/{profileId}/unblock")
    @Operation(summary = "Unblock profile", description = "API using for unblocking")
    public ResponseEntity<AppResponse<String>> unblockProfile(@RequestHeader(value = "Accept-Language", defaultValue = "EN") AppLanguage lang,
                                            @PathVariable("profileId") Integer profileId) {
        return ResponseEntity.ok(profileService.unblockProfile(profileId, lang));
    }

    @PutMapping("/admin/profile/{profileId}/delete")
    @Operation(summary = "Delete profiles", description = "It can be used for deleting profile")
    public ResponseEntity<AppResponse<String>> deleteProfile(@RequestHeader(value = "Accept-Language", defaultValue = "EN") AppLanguage lang,
                                          @PathVariable("profileId") Integer profileId) {
        return ResponseEntity.ok(profileService.deleteProfile(profileId, lang));
    }

}
