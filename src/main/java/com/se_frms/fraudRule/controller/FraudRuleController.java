package com.se_frms.fraudRule.controller;

import com.se_frms.auth.dto.AuthResponseDTO;
import com.se_frms.fraudRule.dto.FraudRuleRequestDTO;
import com.se_frms.fraudRule.dto.FraudRuleResponseDTO;
import com.se_frms.fraudRule.dto.FraudRuleStatusDTO;
import com.se_frms.fraudRule.dto.FraudRuleUpdateDTO;
import com.se_frms.fraudRule.service.FraudRuleService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping(
        "/api/v1/fraud-rule"
)

@RequiredArgsConstructor
public class FraudRuleController {

    private final FraudRuleService service;

    @PostMapping
    public ResponseEntity<?> create(

            @RequestBody
            FraudRuleRequestDTO request

    ) {

        FraudRuleResponseDTO responseData =

                service.create(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        AuthResponseDTO
                                .<FraudRuleResponseDTO>builder()
                                .status(true)
                                .responseCode(201)
                                .responseMessage(
                                        "Fraud Rule created successfully"
                                )
                                .responseData(
                                        responseData
                                )
                                .build()
                );

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(

            @PathVariable
            Integer id,

            @RequestBody
            FraudRuleUpdateDTO request

    ) {

        FraudRuleResponseDTO responseData =

                service.update(

                        id,

                        request

                );

        return ResponseEntity
                .ok(

                        AuthResponseDTO
                                .<FraudRuleResponseDTO>builder()
                                .status(true)
                                .responseCode(200)
                                .responseMessage(
                                        "Fraud Rule updated successfully"
                                )
                                .responseData(
                                        responseData
                                )
                                .build()

                );

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(


@PathVariable
Integer id


    ) {


        service.delete(
                id
        );

        return ResponseEntity.ok(

                AuthResponseDTO
                        .<String>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage(
                                "Fraud Rule deleted successfully"
                        )
                        .responseData(
                                "Deleted Successfully"
                        )
                        .build()

        );


    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(


@PathVariable
Integer id


    ) {


        FraudRuleResponseDTO responseData =

                service.getById(
                        id
                );

        return ResponseEntity.ok(

                AuthResponseDTO
                        .<FraudRuleResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage(
                                "Fraud Rule fetched successfully"
                        )
                        .responseData(
                                responseData
                        )
                        .build()

        );


    }

    @GetMapping
    public ResponseEntity<?> getAll(


Pageable pageable


    ) {


        Page<FraudRuleResponseDTO> responseData =

                service.getAll(
                        pageable
                );

        return ResponseEntity.ok(

                AuthResponseDTO
                        .<Page<FraudRuleResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage(
                                "Fraud Rules fetched successfully"
                        )
                        .responseData(
                                responseData
                        )
                        .build()

        );


    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getByCategoryId(


@PathVariable
Integer categoryId


    ) {


        List<FraudRuleResponseDTO> responseData =

                service.getByCategoryId(
                        categoryId
                );

        return ResponseEntity.ok(

                AuthResponseDTO
                        .<List<FraudRuleResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage(
                                "Fraud Rules fetched successfully"
                        )
                        .responseData(
                                responseData
                        )
                        .build()

        );


    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(


@PathVariable
Integer id,

@RequestBody
FraudRuleStatusDTO request


    ) {


        FraudRuleResponseDTO responseData =

                service.updateStatus(

                        id,

                        request

                );

        return ResponseEntity.ok(

                AuthResponseDTO
                        .<FraudRuleResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage(
                                "Fraud Rule status updated successfully"
                        )
                        .responseData(
                                responseData
                        )
                        .build()

        );

    }



}