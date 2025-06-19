package com.subham.serviceNowIntegration.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDetailsDTO {

    @JsonProperty("item_name")
    private String itemName;

    @JsonProperty("sysparm_id")
    private String sysParamId;

    @JsonProperty("sysparm_quantity")
    private String quantity;

}
