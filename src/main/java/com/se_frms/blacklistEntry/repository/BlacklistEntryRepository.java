package com.se_frms.blacklistEntry.repository;

import com.se_frms.blacklistEntry.model.BlacklistEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BlacklistEntryRepository
        extends JpaRepository<BlacklistEntry, Integer>, JpaSpecificationExecutor<BlacklistEntry> {

    boolean existsByTypeAndValueAndStatus(String type, String value, Boolean status);

    // Same check, ignoring case: fraud matching normalizes values to upper case,
    // so "abc" and "ABC" are the same blacklist entry.
    boolean existsByTypeAndValueIgnoreCaseAndStatus(String type, String value, Boolean status);

    // Same, but ignores the row with this id (used when editing an existing entry).
    boolean existsByTypeAndValueIgnoreCaseAndStatusAndIdNot(String type, String value, Boolean status, Integer id);

    // Duplicate check used by add / edit / re-activate: is there another ACTIVE entry (id <> excludeId)
    // with the same type + value? Ignores case and leading/trailing spaces on both sides.
    @Query("select count(b) > 0 from BlacklistEntry b "
            + "where upper(trim(b.type)) = upper(trim(:type)) "
            + "and upper(trim(b.value)) = upper(trim(:value)) "
            + "and b.status = true and b.id <> :excludeId")
    boolean existsActiveDuplicate(@Param("type") String type,
                                  @Param("value") String value,
                                  @Param("excludeId") Integer excludeId);

    // used by the internal endpoint that rule-cache-service polls
    List<BlacklistEntry> findByStatusOrderByUpdatedAtDesc(Boolean status);
}
