package com.fedexdemo.rating.migration;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "migration.feature-flags")
public record MigrationFeatureFlags(List<String> javaEnabledAccounts) {
}
