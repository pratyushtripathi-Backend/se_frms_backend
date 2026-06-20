package com.se_frms.sms.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Msg24x7MessageResponse {

    @JsonProperty("MessageErrorCode")
    private Integer messageErrorCode;

    @JsonProperty("MessageErrorDescription")
    private String messageErrorDescription;

    @JsonProperty("MobileNumber")
    private String mobileNumber;

    @JsonProperty("MessageId")
    private String messageId;

    @JsonProperty("Custom")
    private String custom;

    public boolean isSuccess() {
        return messageErrorCode != null
                && messageErrorCode == 0;
    }
}