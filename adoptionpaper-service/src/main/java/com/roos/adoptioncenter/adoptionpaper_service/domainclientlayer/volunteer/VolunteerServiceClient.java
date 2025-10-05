package com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.volunteer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roos.adoptioncenter.adoptionpaper_service.utils.ExceptionsHandling.HttpErrorInfo;
import com.roos.adoptioncenter.adoptionpaper_service.utils.ExceptionsHandling.InvalidInputException;
import com.roos.adoptioncenter.adoptionpaper_service.utils.ExceptionsHandling.NotFoundException;
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

    public VolunteerServiceClient(RestTemplate restTemplate,
                                  ObjectMapper mapper,
                                  @Value("${app.volunteer-service.host}") String volunteersServiceHost,
                                  @Value("${app.volunteer-service.port}") String volunteersServicePort
    ) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;

        VOLUNTEERS_SERVICE_BASE_URL = "http://" + volunteersServiceHost + ":" + volunteersServicePort + "/api/v1/volunteers";
    }

    public VolunteerModel getVolunteerById(String volunteerId) {
        try {
            String url = VOLUNTEERS_SERVICE_BASE_URL + "/" + volunteerId;
            log.debug("Volunteers-Service URL is: " + url);
            VolunteerModel volunteerResponseModel = restTemplate.getForObject(url, VolunteerModel.class);
            return volunteerResponseModel;
        }
        catch(HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public List<VolunteerModel> getAllVolunteers() {
        try {
            log.debug("volunteer-service URL is {}", VOLUNTEERS_SERVICE_BASE_URL);

            ResponseEntity<List<VolunteerModel>> response = restTemplate.exchange(VOLUNTEERS_SERVICE_BASE_URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<VolunteerModel>>() {});

            return response.getBody();
        }
        catch(HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public VolunteerModel addVolunteer(VolunteerModel volunteerRequestModel) {
        try{
            String url = VOLUNTEERS_SERVICE_BASE_URL;
            log.debug("Volunteers-Service URL is: " + url);
            VolunteerModel volunteerResponseModel = restTemplate.postForObject(url, volunteerRequestModel, VolunteerModel.class);
            return volunteerResponseModel;
        }
        catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public VolunteerModel updateVolunteer(VolunteerModel volunteerRequestModel, String volunteerId) {
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