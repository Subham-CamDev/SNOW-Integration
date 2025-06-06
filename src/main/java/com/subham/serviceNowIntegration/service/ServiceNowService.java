package com.subham.serviceNowIntegration.service;

import com.subham.serviceNowIntegration.configuration.ServiceNowProperties;
import com.subham.serviceNowIntegration.model.IncidentDetailsDTO;
import com.subham.serviceNowIntegration.model.IncidentResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@Slf4j
public class ServiceNowService {

    private final ServiceNowProperties props;

    private final RestTemplate restTemplate;

    public ServiceNowService(RestTemplate restTemplate, ServiceNowProperties props)
    {
        this.restTemplate = restTemplate;
        this.props = props;
    }

    public void createIncident(IncidentDetailsDTO incident) {

        String endPoint = "https://dev321691.service-now.com/api/now/import/u_spring_to_incident";
        String username = props.getUsername();
        String password = props.getPassword();
        log.info("SNOW Username : {}", username);
        log.info("SNOW Password : {}", password);
        String auth = username + ":" + password;

        //Creating the Authorization Header with username and password with Base64 Password Encoder
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);

        //Creating the Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authHeader);

        //Creating the Incident Details payload
        IncidentDetailsDTO newIncident = new IncidentDetailsDTO();
        newIncident.setCategory(incident.getCategory());
        newIncident.setCallerID(incident.getCallerID());
        newIncident.setShortDescription(incident.getShortDescription());
        newIncident.setDescription(incident.getDescription());

        HttpEntity<IncidentDetailsDTO> request = new HttpEntity<>(newIncident, headers);

        try {
            //Make the POST Call
            ResponseEntity<IncidentResponseDTO> response = restTemplate.exchange(endPoint, HttpMethod.POST, request, IncidentResponseDTO.class);

            //saving the response in the POJO class and iterating over the fields to get the Incident Number and SysID
            IncidentResponseDTO responseBody = response.getBody();
            if (responseBody != null && !responseBody.getResult().isEmpty()) {
                IncidentResponseDTO.Result incidentDetails = responseBody.getResult().get(0);
                log.info("Incident Created: Number = {}, Sys ID = {}",
                        incidentDetails.getDisplayValue(),
                        incidentDetails.getSysId());
            } else {
                log.warn("ServiceNow response did not contain incident details.");
            }
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("ServiceNow returned an error: Status = {}, Body = {}",
                    ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex; // Rethrow or convert to custom exception
        }
        catch (RestClientException e) {
            log.error("Failed to connect to ServiceNow: {}", e.getMessage());
            throw e;
        }
    }
}
