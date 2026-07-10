package com.se_frms.auth.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClientLocationDTO {

    @JsonAlias("lat")
    private BigDecimal latitude;

    @JsonAlias("lng")
    private BigDecimal longitude;
}
