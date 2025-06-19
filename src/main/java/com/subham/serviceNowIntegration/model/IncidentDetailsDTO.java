package com.subham.serviceNowIntegration.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties (ignoreUnknown = true)
public class IncidentDetailsDTO {

    @JsonProperty("u_category")
    private String category;

    @JsonProperty("u_short_description")
    private String shortDescription;

    @JsonProperty("u_description")
    private String description;

    @JsonProperty("u_caller_id")
    private String callerID;
}
