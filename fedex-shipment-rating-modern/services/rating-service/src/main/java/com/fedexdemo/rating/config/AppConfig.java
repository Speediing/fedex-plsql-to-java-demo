package com.fedexdemo.rating.config;

import com.fedexdemo.rating.migration.MigrationFeatureFlags;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MigrationFeatureFlags.class)
public class AppConfig {
}
