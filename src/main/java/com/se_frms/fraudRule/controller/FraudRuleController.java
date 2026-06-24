package com.se_frms.fraudRule.controller;

import com.se_frms.fraudRule.dto.FraudRuleRequestDTO;
import com.se_frms.fraudRule.dto.FraudRuleUpdateDTO;
import com.se_frms.fraudRule.service.FraudRuleService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

        return ResponseEntity.ok(
                service.create(
                        request
                )
        );

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(

            @PathVariable
            Integer id,

            @RequestBody
            FraudRuleUpdateDTO request

    ) {

        return ResponseEntity.ok(

                service.update(

                        id,

                        request

                )

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
                "Fraud Rule deleted successfully"
        );

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(

            @PathVariable
            Integer id

    ) {

        return ResponseEntity.ok(

                service.getById(
                        id
                )

        );

    }

    @GetMapping
    public ResponseEntity<?> getAll(

            Pageable pageable

    ) {

        return ResponseEntity.ok(

                service.getAll(
                        pageable
                )

        );

    }

}