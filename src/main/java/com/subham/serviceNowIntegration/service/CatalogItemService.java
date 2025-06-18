package com.subham.serviceNowIntegration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.subham.serviceNowIntegration.configuration.ServiceNowProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
public class CatalogItemService {

    private final ServiceNowProperties props;

    private final RestTemplate restTemplate;

    public CatalogItemService(RestTemplate restTemplate, ServiceNowProperties props) {
        this.restTemplate = restTemplate;
        this.props = props;
    }

    public String getItemSysId(String itemName) {
        String username = props.getUsername();
        String password = props.getPassword();
        String auth = username + ":" + password;

        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);

        String path = "https://dev321691.service-now.com/api/now/table/sc_cat_item";
        String endpoint = UriComponentsBuilder.fromUriString(path)
                .queryParam("sysparm_query", "name=" + itemName + "^ORDERBYname")
                .queryParam("sysparm_fields", "sys_id,active,name")
                //.queryParam("sysparm_limit", "1")
                .toUriString();

        //log.info("The full URL is : "+endpoint);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", authHeader);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(endpoint, HttpMethod.GET, request, JsonNode.class);

            assert response.getBody() != null;
            JsonNode resultArray = response.getBody().get("result");
            //log.info("The responseArray is : "+resultArray);

            for (JsonNode item : resultArray) {
                if (itemName.equalsIgnoreCase(item.get("name").asText())) {
                    if (item.get("active").asBoolean()) {
                        return item.get("sys_id").asText();
                    } else {
                        throw new RuntimeException("The requested item " + itemName + " is not Active");
                    }
                }
            }
            throw new RuntimeException("Catalog Item not found for name: " + itemName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve catalog item sys_id: " + e.getMessage(), e);
        }
    }
}