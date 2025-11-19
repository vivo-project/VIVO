package edu.cornell.mannlib.vivo.orcid.service;

import java.util.Date;

import edu.cornell.mannlib.vivo.orcid.util.Scheduled;

public class OrcidSyncService {

    @Scheduled(cron = "${orcid.sync.cron}")
    public void syncOrcidProfiles() {
        System.out.println("Syncing ORCID profiles at: " + new Date());
        // TODO: provide implementation
    }

    @Scheduled(fixedRate = "60000") // Every 5 minutes
    public void refreshTokens() {
        System.out.println("Refreshing ORCID tokens: " + new Date());
        // TODO: provide implementation
    }

    @Scheduled(fixedRate = "50000") // Daily at 6 AM
    public void cleanupExpiredTokens() {
        System.out.println("Cleaning up expired tokens at: " + new Date());
        // TODO: provide implementation
    }
}
