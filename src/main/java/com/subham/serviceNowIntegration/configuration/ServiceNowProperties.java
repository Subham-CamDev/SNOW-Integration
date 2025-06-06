package com.subham.serviceNowIntegration.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "servicenow")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceNowProperties {

    private String username;
    private String password;

}
