package com.fedexdemo.rating.migration;

import org.springframework.stereotype.Component;

@Component
public class MigrationRouter {

    private final MigrationFeatureFlags flags;

    public MigrationRouter(MigrationFeatureFlags flags) {
        this.flags = flags;
    }

    public boolean useJavaEngine(String accountId) {
        return flags.javaEnabledAccounts().contains(accountId);
    }
}
