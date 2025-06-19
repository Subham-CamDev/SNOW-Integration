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

@Service
@Slf4j
public class UploadAttachment {

    private final ServiceNowProperties props;

    private final RestTemplate restTemplate;

    public UploadAttachment(RestTemplate restTemplate, ServiceNowProperties props) {
        this.restTemplate = restTemplate;
        this.props = props;
    }

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

        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", authHeader);

        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        try
        {
            ResponseEntity<JsonNode> response = restTemplate.exchange(endpoint, HttpMethod.POST, request, JsonNode.class);

            assert response.getBody() != null;
            JsonNode resultArray = response.getBody().get("result");

            return resultArray.get("sys_id").asText();

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload attachment : "+e.getMessage(),e);
        }
    }

}
