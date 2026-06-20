package com.se_frms.blackListUser.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RemoveBlackListRequestDTO {

    @NotNull(message = "User id is required")
    private Integer userId;
}
