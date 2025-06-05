package com.subham.serviceNowIntegration.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class IncidentResponseDTO {

    @JsonProperty("import_set")
    private String importSet;

    @JsonProperty("staging_table")
    private String stagingTable;

    private List<Result> result;

    @Data
    public static class Result {
        @JsonProperty("transform_map")
        private String transformMap;

        private String table;

        @JsonProperty("display_name")
        private String displayName;

        @JsonProperty("display_value")
        private String displayValue; // This is the Incident Number

        @JsonProperty("record_link")
        private String recordLink;

        private String status;

        @JsonProperty("sys_id")
        private String sysId;
    }
}
