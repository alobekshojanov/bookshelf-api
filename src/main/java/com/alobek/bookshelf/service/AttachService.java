package com.alobek.bookshelf.service;

import com.alobek.bookshelf.dto.AttachDTO;
import com.alobek.bookshelf.entity.AttachEntity;
import com.alobek.bookshelf.enums.AppLanguage;
import com.alobek.bookshelf.exps.AppBadException;
import com.alobek.bookshelf.repository.AttachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class AttachService {

    @Value("${attach.upload.folder}")
    private String folderName;
    @Value("${attach.upload.url}")
    private String attachUrl;


    @Autowired
    private AttachRepository attachRepository;

    @Autowired
    private ResourceBundleService bundleService;

    public AttachDTO upload(MultipartFile file, AppLanguage lang) {
        if (file.isEmpty()) {
            throw new AppBadException(bundleService.getMessage("file.not.exist", lang));
        }

        try {
            String pathFolder = getYmDString(); // 2024/09/27
            String key = UUID.randomUUID().toString(); // dasdasd-dasdasda-asdasda-asdasd
            String extension = getExtension(Objects.requireNonNull(file.getOriginalFilename())); // .jpg, .png, .mp4

            // create folder if not exists
            File folder = new File(folderName + "/" + pathFolder);
            if (!folder.exists()) {
                boolean t = folder.mkdirs();
            }

            // save to system
            byte[] bytes = file.getBytes();
            Path path = Paths.get(folderName + "/" + pathFolder + "/" + key + "." + extension);
            Files.write(path, bytes);

            // save to db
            AttachEntity entity = new AttachEntity();
            entity.setId(key + "." + extension);
            entity.setPath(pathFolder);
            entity.setSize(file.getSize());
            entity.setOriginName(file.getOriginalFilename());
            entity.setExtension(extension);
            entity.setVisible(true);
            attachRepository.save(entity);

            return toDTO(entity);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResponseEntity<Resource> open(String id) {
        AttachEntity entity = getEntity(id);
        Path filePath = Paths.get(getPath(entity)).normalize();
        Resource resource = null;
        try {
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new AppBadException("File not found: " + id);
            }
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream"; // Fallback content type
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private String getYmDString() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DATE);
        return year + "/" + month + "/" + day;
    }

    public AttachEntity getEntity(String id) {
        Optional<AttachEntity> optional = attachRepository.findById(id);
        if (optional.isEmpty()) {
            throw new AppBadException("File not found");
        }
        return optional.get();
    }

    private String getPath(AttachEntity entity) {
        return folderName + "/" + entity.getPath() + "/" + entity.getId();
    }

    private String getExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");
        return fileName.substring(lastIndex + 1);
    }

    public String openURL(String fileName) {
        return attachUrl + "/open/" + fileName;
    }

    private AttachDTO toDTO(AttachEntity entity) {
        AttachDTO attachDTO = new AttachDTO();
        attachDTO.setId(entity.getId());
        attachDTO.setOriginName(entity.getOriginName());
        attachDTO.setSize(entity.getSize());
        attachDTO.setExtension(entity.getExtension());
        attachDTO.setCreatedDate(entity.getCreatedDate());
        attachDTO.setUrl(openURL(entity.getId()));
        return attachDTO;
    }

    public boolean delete(String id) {
        AttachEntity entity = getEntity(id);
        attachRepository.changeVisibility(entity.getId());
        File file = new File(getPath(entity));
        boolean b = false;
        if (file.exists()) {
            b = file.delete();
        }
        return b;
    }

    public AttachDTO attachDTO(String photoId) {
        if (photoId == null) return null;

        AttachDTO attachDTO = new AttachDTO();
        attachDTO.setId(photoId);
        attachDTO.setUrl(openURL(photoId));

        return attachDTO;
    }
}
