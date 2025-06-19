package com.subham.serviceNowIntegration.service;

import com.subham.serviceNowIntegration.configuration.ServiceNowProperties;
import com.subham.serviceNowIntegration.model.IncidentDetailsDTO;
import com.subham.serviceNowIntegration.model.IncidentResponseDTO;
import com.subham.serviceNowIntegration.model.RequestDetailsDTO;
import com.subham.serviceNowIntegration.model.RequestResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@Slf4j
public class ServiceNowService {

    private final ServiceNowProperties props;

    private final RestTemplate restTemplate;

    private final CatalogItemService itemService;

    private final UploadAttachment uploadService;

    public ServiceNowService(RestTemplate restTemplate, ServiceNowProperties props, CatalogItemService itemService,
                             UploadAttachment uploadService)
    {
        this.restTemplate = restTemplate;
        this.props = props;
        this.itemService = itemService;
        this.uploadService = uploadService;
    }

    public String createIncident(IncidentDetailsDTO incident, MultipartFile file) {

        String endPoint = "https://dev321691.service-now.com/api/now/import/u_spring_to_incident";
        String username = props.getUsername();
        String password = props.getPassword();
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
                String attachmentSysId = uploadService.sendAttachmentToSnow(incidentDetails.getTable(), incidentDetails.getSysId(), file);
                return incidentDetails.getDisplayValue();
            } else {
                log.warn("ServiceNow response did not contain incident details.");
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("ServiceNow returned an error: Status = {}, Body = {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw e; // Rethrow or convert to custom exception
        }
        catch (RestClientException e) {
            log.error("Failed to connect to ServiceNow: {}", e.getMessage());
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public String createRequest(RequestDetailsDTO request)
    {
        String endPoint = "https://dev321691.service-now.com/api/sn_sc/servicecatalog/items/sys_id/order_now";
        String username = props.getUsername();
        String password = props.getPassword();
        String auth = username + ":" + password;

        //Creating the Authorization Header with username and password with Base64 Password Encoder
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);

        //get the sys_id of the Item from its name
        String itemName = request.getItemName();
        String itemSysId = itemService.getItemSysId(itemName);

        //Creating the Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authHeader);

        //Creating Item for Request Creation
        RequestDetailsDTO newRequest = new RequestDetailsDTO();
        newRequest.setQuantity(request.getQuantity());
        newRequest.setSysParamId(itemSysId);

        HttpEntity<RequestDetailsDTO> requestBody = new HttpEntity<>(newRequest, headers);

        try{
            //Make the POST Call
            ResponseEntity<RequestResponseDTO> response = restTemplate.exchange(endPoint.replace("sys_id", itemSysId), HttpMethod.POST, requestBody, RequestResponseDTO.class);

            RequestResponseDTO responseBody = response.getBody();

            if (responseBody != null && responseBody.getResult() != null) {
                RequestResponseDTO.Result result = responseBody.getResult();
                log.info("Request Created: Number = {}, Sys ID = {}",
                        result.getRequestNumber(),
                        result.getRequestSysId());
                return result.getRequestNumber();
            } else {
                log.warn("ServiceNow response did not contain Request details.");
            }
        }catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("ServiceNow returned an error: Status = {}, Body = {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw e; // Rethrow or convert to custom exception
        }
        catch (RestClientException e) {
            log.error("Failed to connect to ServiceNow: {}", e.getMessage());
            throw e;
        }
        return null;
    }
}
