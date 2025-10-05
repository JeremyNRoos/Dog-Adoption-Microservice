package com.roos.adoptioncenter.apigateway.DomainClientLayer.adopter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.HttpErrorInfo;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.InvalidInputException;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
import com.roos.adoptioncenter.apigateway.presentationlayer.adopter.AdopterRequestModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.adopter.AdopterResponseModel;


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
public class AdopterServiceClient {

    private final RestTemplate restTemplate;


    private final ObjectMapper mapper;

    private final String ADOPTERS_SERVICE_BASE_URL;

    public AdopterServiceClient(  RestTemplate restTemplate,
                                    ObjectMapper mapper,
                                    @Value("${app.adopter-service.host}") String adoptersServiceHost,
                                    @Value("${app.adopter-service.port}") String adoptersServicePort
    ) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;

        ADOPTERS_SERVICE_BASE_URL = "http://" + adoptersServiceHost + ":" + adoptersServicePort + "/api/v1/adopters";
    }

    public AdopterResponseModel getAdopterById(String adopterId) {
        try {
            String url = ADOPTERS_SERVICE_BASE_URL + "/" + adopterId;
            log.debug("Adopters-Service URL is: " + url);
            AdopterResponseModel adopterResponseModel = restTemplate.getForObject(url, AdopterResponseModel.class);
            return adopterResponseModel;
        }
        catch(HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public List<AdopterResponseModel> getAllAdopters() {
        try {
            log.debug("adopter-service URL is {}", ADOPTERS_SERVICE_BASE_URL);

            ResponseEntity<List<AdopterResponseModel>> response = restTemplate.exchange(ADOPTERS_SERVICE_BASE_URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<AdopterResponseModel>>() {});

            return response.getBody();
        }
        catch(HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public AdopterResponseModel addAdopter(AdopterRequestModel adopterRequestModel) {
        try{
            String url = ADOPTERS_SERVICE_BASE_URL;
            log.debug("Adopters-Service URL is: " + url);
            AdopterResponseModel adopterResponseModel = restTemplate.postForObject(url, adopterRequestModel, AdopterResponseModel.class);
            return adopterResponseModel;
        }
        catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public AdopterResponseModel updateAdopter(AdopterRequestModel adopterRequestModel, String adopterId) {
        try {
            String url = ADOPTERS_SERVICE_BASE_URL + "/" + adopterId;
            log.debug("Adopters-Service URL is: " + url);
            restTemplate.put(url, adopterRequestModel);
            return getAdopterById(adopterId);
        }
        catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }



    public void deleteAdopter(String adopterId) {
        try {
            String url = ADOPTERS_SERVICE_BASE_URL + "/" + adopterId;
            log.debug("Adopters-Service URL is: " + url);
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