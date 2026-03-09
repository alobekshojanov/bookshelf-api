package com.alobek.bookshelf.controller;


import com.alobek.bookshelf.dto.AttachDTO;
import com.alobek.bookshelf.enums.AppLanguage;
import com.alobek.bookshelf.service.AttachService;
import jakarta.mail.Quota;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/attach")
public class AttachController {

    @Autowired
    private AttachService attachService;


    @PostMapping("/upload")
    public ResponseEntity<AttachDTO> create(@RequestParam("file") MultipartFile file,
                                            @RequestHeader(value = "Accept-Language", defaultValue = "EN")AppLanguage lang) {
        return ResponseEntity.ok(attachService.upload(file, lang));
    }

    @GetMapping("/open/{fileName}")
    public ResponseEntity<Resource> open(@PathVariable String fileName){
        return attachService.open(fileName);
    }

}
