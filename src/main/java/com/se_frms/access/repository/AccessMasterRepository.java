package com.se_frms.access.repository;

import com.se_frms.access.model.AccessMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccessMasterRepository
        extends JpaRepository<AccessMaster,Integer> {

    Optional<AccessMaster>
    findByAccessName(
            String accessName
    );




}