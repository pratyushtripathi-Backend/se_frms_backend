package com.se_frms.access.service;

import com.se_frms.access.dto.AccessRequestDTO;
import com.se_frms.access.dto.AccessResponseDTO;
import com.se_frms.access.model.AccessMaster;
import com.se_frms.access.repository.AccessMasterRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccessServiceImpl
        implements AccessService {

    private final AccessMasterRepository repository;

    @Override
    public AccessResponseDTO create(
            AccessRequestDTO request
    ) {

        repository
                .findByAccessName(
                        request.getAccessName()
                )

                .ifPresent(v -> {

                    throw new RuntimeException(
                            "Access already exists"
                    );

                });

        AccessMaster entity =
                AccessMaster
                        .builder()

                        .accessName(
                                request.getAccessName()
                        )

                        .status(
                                true
                        )

                        .build();

        return map(

                repository.save(
                        entity
                )

        );

    }

    @Override
    public List<AccessResponseDTO> getAll() {

        return repository
                .findAll()

                .stream()

                .map(this::map)

                .toList();

    }

    @Override
    public AccessResponseDTO getById(
            Integer id
    ) {

        return map(

                repository
                        .findById(id)

                        .orElseThrow(

                                () -> new RuntimeException(
                                        "Access not found"
                                )
                        )

        );

    }

    @Override
    public AccessResponseDTO update(
            Integer id,
            AccessRequestDTO request
    ) {

        AccessMaster entity =
                repository
                        .findById(id)

                        .orElseThrow(

                                () -> new RuntimeException(
                                        "Access not found"
                                )

                        );

        repository
                .findByAccessName(
                        request.getAccessName()
                )

                .ifPresent(access -> {

                    if (!access.getId().equals(id)) {

                        throw new RuntimeException(
                                "Access already exists"
                        );

                    }

                });

        entity.setAccessName(
                request.getAccessName()
        );

        entity.setStatus(
                request.getStatus()
        );

        entity.setUpdatedAt(
                LocalDateTime.now()
        );

        return map(

                repository.save(
                        entity
                )

        );

    }

    @Override
    public String updateStatus(
            Integer id,
            Boolean status
    ) {

        AccessMaster entity =
                repository
                        .findById(id)

                        .orElseThrow(

                                () -> new RuntimeException(
                                        "Access not found"
                                )

                        );

        entity.setStatus(
                status
        );

        entity.setUpdatedAt(
                LocalDateTime.now()
        );

        repository.save(
                entity
        );

        return status

                ?

                "Access enabled successfully"

                :

                "Access disabled successfully";

    }

    @Override
    public String delete(
            Integer id
    ) {

        AccessMaster entity =
                repository
                        .findById(id)

                        .orElseThrow(

                                () -> new RuntimeException(
                                        "Access not found"
                                )

                        );

        entity.setStatus(
                false
        );

        entity.setUpdatedAt(
                LocalDateTime.now()
        );

        repository.save(
                entity
        );

        return "Access deleted successfully";

    }

    private AccessResponseDTO map(
            AccessMaster entity
    ) {

        return AccessResponseDTO
                .builder()

                .id(
                        entity.getId()
                )

                .accessName(
                        entity.getAccessName()
                )

                .status(
                        entity.getStatus()
                )

                .createdDate(
                        entity.getCreatedDate()
                )

                .updatedAt(
                        entity.getUpdatedAt()
                )

                .build();

    }

}