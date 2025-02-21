package com.azati.warshipprocessing.util;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StringCryptoConvertorConfig {

    @Value("${jasypt.encryptor.password}")
    private String password;

    @Bean
    public StandardPBEStringEncryptor encryptor() {
        var encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(password);
        return encryptor;
    }
}