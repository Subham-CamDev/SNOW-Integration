package com.subham.serviceNowIntegration.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestResponseDTO {

    @JsonProperty("result")
    private Result result;

    @Data
    public static class Result{

        @JsonProperty("request_number")
        private String requestNumber;

        @JsonProperty("request_sys_id")
        private String requestSysId;

        @JsonProperty("item_number")
        private String itemNumber;

        @JsonProperty("item_sys_id")
        private String itemSysId;

    }
}
