package com.se_frms.access.repository;

import com.se_frms.access.model.AccessMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AccessMasterRepository
        extends JpaRepository<AccessMaster, Integer>,
        JpaSpecificationExecutor<AccessMaster> {

    Optional<AccessMaster>
    findByAccessName(
            String accessName
    );




}
