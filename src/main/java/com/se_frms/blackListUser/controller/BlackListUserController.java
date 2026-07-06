package com.se_frms.blackListUser.controller;

import com.se_frms.auth.dto.AuthResponseDTO;
import com.se_frms.blackListUser.dto.BlackListUserRequestDTO;
import com.se_frms.blackListUser.dto.BlackListUserResponseDTO;
import com.se_frms.blackListUser.dto.RemoveBlackListRequestDTO;
import com.se_frms.blackListUser.service.BlackListUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.se_frms.common.dto.PagedResponseDTO;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/blacklist-users")
@RequiredArgsConstructor
public class BlackListUserController {

    private final BlackListUserService blackListUserService;

    @PostMapping
    public ResponseEntity<AuthResponseDTO<BlackListUserResponseDTO>>
    blackListUser(
            @Valid
            @RequestBody
            BlackListUserRequestDTO request
    ) {

        log.info("Blacklist user request received, userId={}", request.getUserId());

        BlackListUserResponseDTO responseData =
                blackListUserService.blackListUser(request);

        log.info("User blacklisted successfully, userId={}", request.getUserId());

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<BlackListUserResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("User blacklisted successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @PatchMapping("/remove")
    public ResponseEntity<AuthResponseDTO<BlackListUserResponseDTO>>
    removeBlackList(
            @Valid
            @RequestBody
            RemoveBlackListRequestDTO request
    ) {

        log.info("Remove blacklist request received, userId={}", request.getUserId());

        BlackListUserResponseDTO responseData =
                blackListUserService.removeBlackList(request);

        log.info("User blacklist removed successfully, userId={}", request.getUserId());

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<BlackListUserResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("User blacklist removed successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<AuthResponseDTO<PagedResponseDTO<BlackListUserResponseDTO>>>
    getAllBlackListUsers(
            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam
            Map<String, String> filters
    ) {

        log.info(
                "Fetch all blacklist users request received, page={}, size={}",
                page,
                size
        );

        Page<BlackListUserResponseDTO> pageData =
                blackListUserService.getAllBlackListUsers(
                        page,
                        size,
                        filters
                );

        PagedResponseDTO<BlackListUserResponseDTO> responseData =
                PagedResponseDTO.from(pageData);

        log.info(
                "Blacklist users fetched successfully, count={}",
                pageData.getNumberOfElements()
        );

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<PagedResponseDTO<BlackListUserResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Blacklist users fetched successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthResponseDTO<BlackListUserResponseDTO>>
    getBlackListUserById(
            @PathVariable
            Integer id
    ) {

        log.info("Fetch blacklist user request received, id={}", id);

        BlackListUserResponseDTO responseData =
                blackListUserService.getBlackListUserById(id);

        log.info("Blacklist user fetched successfully, id={}", id);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<BlackListUserResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Blacklist user fetched successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<AuthResponseDTO<PagedResponseDTO<BlackListUserResponseDTO>>>
    getBlackListUsersByUserId(
            @PathVariable
            Integer userId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam
            Map<String, String> filters
    ) {

        log.info(
                "Fetch blacklist users by userId request received, userId={}, page={}, size={}",
                userId,
                page,
                size
        );

        Page<BlackListUserResponseDTO> pageData =
                blackListUserService.getBlackListUsersByUserId(
                        userId,
                        page,
                        size,
                        filters
                );

        PagedResponseDTO<BlackListUserResponseDTO> responseData =
                PagedResponseDTO.from(pageData);

        log.info(
                "Blacklist users by userId fetched successfully, userId={}, count={}",
                userId,
                pageData.getNumberOfElements()
        );

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<PagedResponseDTO<BlackListUserResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Blacklist users fetched successfully")
                        .responseData(responseData)
                        .build()
        );
    }
}
