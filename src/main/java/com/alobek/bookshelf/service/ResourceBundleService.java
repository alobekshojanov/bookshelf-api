package com.alobek.bookshelf.service;

import com.alobek.bookshelf.enums.AppLanguage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class ResourceBundleService {

    @Autowired
    private ResourceBundleMessageSource bundleMessageSource;


    public String getMessage(String code, AppLanguage lang){
        return bundleMessageSource.getMessage(code, null, new Locale(lang.name()));
    }


}
