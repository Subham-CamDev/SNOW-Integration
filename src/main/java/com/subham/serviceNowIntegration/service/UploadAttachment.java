package com.subham.serviceNowIntegration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.subham.serviceNowIntegration.configuration.ServiceNowProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UploadAttachment {

    private final ServiceNowProperties props;

    private final RestTemplate restTemplate;

    public UploadAttachment(RestTemplate restTemplate, ServiceNowProperties props) {
        this.restTemplate = restTemplate;
        this.props = props;
    }

    //Creating the Servicenow Endpoint
    public String sendAttachmentToSnow(String tableName, String recordSysId, MultipartFile file) throws IOException {
        String path = "https://dev321691.service-now.com/api/now/attachment/file";
        String endpoint = UriComponentsBuilder.fromUriString(path)
                .queryParam("table_name", tableName)
                .queryParam("table_sys_id", recordSysId)
                .queryParam("file_name", file.getOriginalFilename())
                .toUriString();

        String username = props.getUsername();
        String password = props.getPassword();
        String auth = username + ":" + password;

        //Encoding the username and password
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);

        //Creating Request Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", authHeader);

        //Creating the Request Body with Binary File data
        HttpEntity<byte[]> request = new HttpEntity<>(file.getBytes(), headers);

        //log.info("Final headers: {}", headers);
        //log.info("Uploading file: {}, with detected type: {}", file.getOriginalFilename(), file.getContentType());


        try
        {
            //Sending the Request to ServiceNow
            ResponseEntity<JsonNode> response = restTemplate.exchange(endpoint, HttpMethod.POST, request, JsonNode.class);

            assert response.getBody() != null;
            JsonNode resultArray = response.getBody().get("result");

            //Checking if successful and sending back the attachment sysID
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().get("result").get("sys_id").asText();
            } else {
                throw new RuntimeException("Failed to upload attachment. Response: " + response);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload attachment : "+e.getMessage(),e);
        }
    }
}
