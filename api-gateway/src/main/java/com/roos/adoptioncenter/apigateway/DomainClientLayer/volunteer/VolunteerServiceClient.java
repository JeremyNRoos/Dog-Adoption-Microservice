package com.roos.adoptioncenter.apigateway.DomainClientLayer.volunteer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.HttpErrorInfo;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.InvalidInputException;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
import com.roos.adoptioncenter.apigateway.presentationlayer.volunteer.VolunteerRequestModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.volunteer.VolunteerResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Slf4j
@Component
public class VolunteerServiceClient {

    private final RestTemplate restTemplate;


    private final ObjectMapper mapper;

    private final String VOLUNTEERS_SERVICE_BASE_URL;

    public VolunteerServiceClient(  RestTemplate restTemplate,
                                   ObjectMapper mapper,
                                   @Value("${app.volunteer-service.host}") String volunteersServiceHost,
                                   @Value("${app.volunteer-service.port}") String volunteersServicePort
    ) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;

        VOLUNTEERS_SERVICE_BASE_URL = "http://" + volunteersServiceHost + ":" + volunteersServicePort + "/api/v1/volunteers";
    }

    public VolunteerResponseModel getVolunteerById(String volunteerId) {
        try {
            String url = VOLUNTEERS_SERVICE_BASE_URL + "/" + volunteerId;
            log.debug("Volunteers-Service URL is: " + url);
            VolunteerResponseModel volunteerResponseModel = restTemplate.getForObject(url, VolunteerResponseModel.class);
            return volunteerResponseModel;
        }
        catch(HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public List<VolunteerResponseModel> getAllVolunteers() {
        try {
            log.debug("volunteer-service URL is {}", VOLUNTEERS_SERVICE_BASE_URL);

            ResponseEntity<List<VolunteerResponseModel>> response = restTemplate.exchange(VOLUNTEERS_SERVICE_BASE_URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<VolunteerResponseModel>>() {});

            return response.getBody();
        }
        catch(HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public VolunteerResponseModel addVolunteer(VolunteerRequestModel volunteerRequestModel) {
        try{
            String url = VOLUNTEERS_SERVICE_BASE_URL;
            log.debug("Volunteers-Service URL is: " + url);
            VolunteerResponseModel volunteerResponseModel = restTemplate.postForObject(url, volunteerRequestModel, VolunteerResponseModel.class);
            return volunteerResponseModel;
        }
        catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public VolunteerResponseModel updateVolunteer(VolunteerRequestModel volunteerRequestModel, String volunteerId) {
        try {
            String url = VOLUNTEERS_SERVICE_BASE_URL + "/" + volunteerId;
            log.debug("Volunteers-Service URL is: " + url);
            restTemplate.put(url, volunteerRequestModel);
            return getVolunteerById(volunteerId);
        }
        catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public void deleteVolunteer(String volunteerId) {
        try {
            String url = VOLUNTEERS_SERVICE_BASE_URL + "/" + volunteerId;
            log.debug("Volunteers-Service URL is: " + url);
            restTemplate.delete(url);
        }
        catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }


    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        }
        catch (IOException ioex) {
            return ioex.getMessage();
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {

        if (ex.getStatusCode() == NOT_FOUND) {
            return new NotFoundException(getErrorMessage(ex));
        }
        if (ex.getStatusCode() == UNPROCESSABLE_ENTITY) {
            return new InvalidInputException(getErrorMessage(ex));
        }

        log.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
        log.warn("Error body: {}", ex.getResponseBodyAsString());
        return ex;
    }




}