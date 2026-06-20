package com.se_frms.sms.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Msg24x7SmsResponse {

    @JsonProperty("ErrorCode")
    private Integer errorCode;

    @JsonProperty("ErrorDescription")
    private String errorDescription;

    @JsonProperty("Data")
    private List<Msg24x7MessageResponse> data;

    public boolean isSuccess() {

        if (errorCode == null || errorCode != 0) {
            return false;
        }

        if (data == null || data.isEmpty()) {
            return true;
        }

        return data.stream()
                .allMatch(Msg24x7MessageResponse::isSuccess);
    }
}