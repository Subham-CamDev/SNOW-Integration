package com.subham.serviceNowIntegration.controller;

import com.subham.serviceNowIntegration.model.IncidentDetailsDTO;
import com.subham.serviceNowIntegration.service.ServiceNowService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/snow")
public class ServiceNowController {

private final ServiceNowService service;

public ServiceNowController(ServiceNowService service)
{
    this.service = service;
}

@PostMapping("/incident")
public ResponseEntity<?> createSnowIncident(@RequestBody IncidentDetailsDTO incident)
{
    service.createIncident(incident);
    return new ResponseEntity<>("Incident Created Successfully", HttpStatus.CREATED);
}
}
