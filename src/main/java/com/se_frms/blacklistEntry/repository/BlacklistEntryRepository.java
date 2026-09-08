package com.se_frms.blacklistEntry.repository;

import com.se_frms.blacklistEntry.model.BlacklistEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BlacklistEntryRepository
        extends JpaRepository<BlacklistEntry, Integer>, JpaSpecificationExecutor<BlacklistEntry> {

    boolean existsByTypeAndValueAndStatus(String type, String value, Boolean status);

    // used by the internal endpoint that rule-cache-service polls
    List<BlacklistEntry> findByStatusOrderByUpdatedAtDesc(Boolean status);
}
