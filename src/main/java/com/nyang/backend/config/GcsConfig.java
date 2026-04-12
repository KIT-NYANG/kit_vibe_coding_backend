package com.nyang.backend.config;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
public class GcsConfig {

    @Bean
    public Storage storage() {
        return StorageOptions.getDefaultInstance().getService();
    }
}