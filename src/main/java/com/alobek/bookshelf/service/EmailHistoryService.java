package com.alobek.bookshelf.service;

import com.alobek.bookshelf.entity.EmailHistoryEntity;
import com.alobek.bookshelf.enums.AppLanguage;
import com.alobek.bookshelf.enums.SmsType;
import com.alobek.bookshelf.exps.AppBadException;
import com.alobek.bookshelf.repository.EmailHistoryRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EmailHistoryService {

    @Autowired
    private EmailHistoryRepository emailHistoryRepository;

    @Autowired
    private ResourceBundleService bundleService;

    public void create(String email, String code, SmsType smsType) {

        EmailHistoryEntity entity = new EmailHistoryEntity();
        entity.setEmail(email);
        entity.setCode(code);
        entity.setEmailType(smsType);
        entity.setAttemptCount(0);
        entity.setCreatedDate(LocalDateTime.now());
        emailHistoryRepository.save(entity);
    }

    public Long getEmailCount(String email){
        LocalDateTime now = LocalDateTime.now();
        return  emailHistoryRepository.countByEmailAndCreatedDateBetween(email, now.minusMinutes(1), now);
    }

    public void check(String tempUsername, String code, AppLanguage lang) {

        Optional<EmailHistoryEntity> optional = emailHistoryRepository.findTop1ByEmailOrderByCreatedDateDesc(tempUsername);
        if (optional.isEmpty()){
            throw new AppBadException(bundleService.getMessage("verification.failed", lang));
        }

        EmailHistoryEntity entity = optional.get();
        // attempt count ...
        if (entity.getAttemptCount() >=3){
            throw new AppBadException(bundleService.getMessage("verification.failed", lang));
        }

        // check code ...
        if (!entity.getCode().equals(code)) {
            emailHistoryRepository.updateAttemptCount(entity.getId()); // update attempt count!
            throw new AppBadException(bundleService.getMessage("verification.failed", lang));
        }

        // check time ...
        LocalDateTime expDate = entity.getCreatedDate().plusMinutes(2);
        if (LocalDateTime.now().isAfter(expDate)) {
            throw new AppBadException(bundleService.getMessage("verification.failed", lang));
        }
    }
}
