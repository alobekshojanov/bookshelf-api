package com.alobek.bookshelf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

@Configuration
public class AppConfig {

    @Bean
    public ResourceBundleMessageSource resourceBundleMessageSource() {

        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages/message");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(new Locale("en"));
        messageSource.setUseCodeAsDefaultMessage(true);

        return  messageSource;
    }

}
