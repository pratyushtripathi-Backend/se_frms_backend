package com.se_frms.blacklistEntry.controller;

import com.se_frms.auth.dto.AuthResponseDTO;
import com.se_frms.blacklistEntry.dto.BlacklistEntryRequestDTO;
import com.se_frms.blacklistEntry.dto.BlacklistEntryResponseDTO;
import com.se_frms.blacklistEntry.service.BlacklistEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/blacklist-entries")
@RequiredArgsConstructor
public class BlacklistEntryController {

    private final BlacklistEntryService blacklistEntryService;

    @PostMapping
    public ResponseEntity<AuthResponseDTO<BlacklistEntryResponseDTO>> addEntry(
            @Valid @RequestBody BlacklistEntryRequestDTO request
    ) {

        BlacklistEntryResponseDTO responseData = blacklistEntryService.addEntry(request);

        return ResponseEntity.ok(
                AuthResponseDTO.<BlacklistEntryResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Blacklist entry added successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @PatchMapping("/{id}/remove")
    public ResponseEntity<AuthResponseDTO<BlacklistEntryResponseDTO>> removeEntry(
            @PathVariable Integer id
    ) {

        BlacklistEntryResponseDTO responseData = blacklistEntryService.removeEntry(id);

        return ResponseEntity.ok(
                AuthResponseDTO.<BlacklistEntryResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Blacklist entry removed successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<AuthResponseDTO<Page<BlacklistEntryResponseDTO>>> getAllEntries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String value
    ) {

        Map<String, String> filters = new HashMap<>();
        if (type != null) filters.put("type", type.toUpperCase());
        if (status != null) filters.put("status", status);
        if (value != null) filters.put("value", value);

        Page<BlacklistEntryResponseDTO> responseData =
                blacklistEntryService.getAllEntries(page, size, filters);

        return ResponseEntity.ok(
                AuthResponseDTO.<Page<BlacklistEntryResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Blacklist entries fetched successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthResponseDTO<BlacklistEntryResponseDTO>> getEntryById(
            @PathVariable Integer id
    ) {

        BlacklistEntryResponseDTO responseData = blacklistEntryService.getEntryById(id);

        return ResponseEntity.ok(
                AuthResponseDTO.<BlacklistEntryResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Blacklist entry fetched successfully")
                        .responseData(responseData)
                        .build()
        );
    }
}
