package com.subham.serviceNowIntegration.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subham.serviceNowIntegration.model.IncidentDetailsDTO;
import com.subham.serviceNowIntegration.model.RequestDetailsDTO;
import com.subham.serviceNowIntegration.service.CatalogItemService;
import com.subham.serviceNowIntegration.service.ServiceNowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/snow")
@Slf4j
public class ServiceNowController {

    private final ServiceNowService service;
    private final CatalogItemService itemService;

    public ServiceNowController(ServiceNowService service, CatalogItemService itemService)
    {
        this.service = service;
        this.itemService = itemService;
    }

    @PostMapping(value = "/incident", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> createSnowIncident(@RequestPart("incident") @Validated String incidentJson,
                                                     @RequestPart("attachment") MultipartFile file) throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();
        IncidentDetailsDTO incident = mapper.readValue(incidentJson, IncidentDetailsDTO.class);

        String ID = service.createIncident(incident, file);
        if (ID != null) {
            return new ResponseEntity<>("Incident " + ID + " Created Successfully", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Incident creation failed!!", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/request", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> createSnowRequest(@RequestPart("request") @Validated String requestJson,
                                                    @RequestPart("attachment") MultipartFile file) throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();
        RequestDetailsDTO request = mapper.readValue(requestJson, RequestDetailsDTO.class);

        String ID = service.createRequest(request, file);
        if (ID != null) {
            return new ResponseEntity<>("Request " + ID + " Created Successfully", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Request creation failed!!", HttpStatus.BAD_REQUEST);
        }
    }
}
