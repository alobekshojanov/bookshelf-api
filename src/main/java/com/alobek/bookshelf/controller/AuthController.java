package com.alobek.bookshelf.controller;

import com.alobek.bookshelf.dto.AppResponse;
import com.alobek.bookshelf.dto.AuthDTO;
import com.alobek.bookshelf.dto.ProfileDTO;
import com.alobek.bookshelf.dto.RegistrationDTO;
import com.alobek.bookshelf.dto.auth.ResetPasswordConfirmDTO;
import com.alobek.bookshelf.dto.auth.ResetPasswordDTO;
import com.alobek.bookshelf.enums.AppLanguage;
import com.alobek.bookshelf.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;

@RestController
@RequestMapping("/auth")
@Tag(name = "Controller", description = "Controller for Authorization and Authentication")
@Slf4j
public class AuthController {

    //private static Logger log = LoggerFactory.getLogger(AuthController.class);


    @Autowired
    private AuthService authService;

    @PostMapping("/registration")
    @Operation(summary = "Profile registration", description = "API using for registration")
    public ResponseEntity<AppResponse<String>> registration(@Valid @RequestBody RegistrationDTO dto,
                                               @RequestHeader(value = "Accept-Language", defaultValue = "EN")AppLanguage lang){
        log.info("Registration: name: {}, username: {}", dto.getName(), dto.getUsername());
        return ResponseEntity.ok().body(authService.registration(dto, lang));
    }

    @GetMapping("/registration/verification/{token}")
    @Operation(summary = "Registration verification", description = "API using for registration verification")
    public ResponseEntity<AppResponse<String>> regVerification(@PathVariable("token") String token,
                                                  @RequestParam("lang") AppLanguage lang){
        log.info("Registration email verification: token: {}", token);
        return ResponseEntity.ok().body(authService.regVerification(token, lang));
    }

    @PostMapping("/login")
    @Operation(summary = "Login Profile", description = "API using for login")
    public ResponseEntity<ProfileDTO> login(@Valid @RequestBody AuthDTO dto,
                                            @RequestHeader(value = "Accept-Language", defaultValue = "EN") AppLanguage lang ){
        log.info("Login: {}", dto.getUsername());
        return ResponseEntity.ok().body(authService.login(dto, lang));
    }

    @PostMapping("/reset-password")
    @Operation(summary = " Reset Password", description = "API using for reset password")
    public ResponseEntity<AppResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordDTO dto,
                                                             @RequestHeader(value = "Accept-Language", defaultValue = "EN") AppLanguage lang ){
        return ResponseEntity.ok().body(authService.resetPassword(dto, lang));
    }

    @PostMapping("/reset-password-confirm")
    @Operation(summary = " Confirmation to reset password", description = "API using for confirming to reset password")
    public ResponseEntity<AppResponse<String>> resetPasswordConfirm(@Valid @RequestBody ResetPasswordConfirmDTO dto,
                                                                    @RequestHeader(value = "Accept-Language", defaultValue = "EN") AppLanguage lang ){
        return ResponseEntity.ok().body(authService.resetPasswordConfirm(dto, lang));
    }

}
