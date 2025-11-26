package com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.dog;


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
public class DogServiceClient {

    private final RestTemplate restTemplate;


    private final ObjectMapper mapper;

    private final String DOGS_SERVICE_BASE_URL;

    public DogServiceClient(RestTemplate restTemplate,
                            ObjectMapper mapper,
                            @Value("${app.dogandlocation-service.host}") String dogsServiceHost,
                            @Value("${app.dogandlocation-service.port}") String dogsServicePort
    ) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;

        DOGS_SERVICE_BASE_URL = "http://" + dogsServiceHost + ":" + dogsServicePort + "/api/v1/locations";
    }

    public DogModel getDogById(String locationId, String dogId) {
        try {
            String url = DOGS_SERVICE_BASE_URL + "/" + locationId + "/" + "dogs" + "/" + dogId;
            log.debug("Dogs-Service URL is: " + url);
            DogModel dogResponseModel = restTemplate.getForObject(url, DogModel.class);
            return dogResponseModel;
        }
        catch(HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public List<DogModel> getAllDogs(String locationId) {
        try {
            String url = DOGS_SERVICE_BASE_URL + "/" + locationId + "/" + "dogs" ;
            log.debug("customer-service URL is {}", DOGS_SERVICE_BASE_URL);

            ResponseEntity<List<DogModel>> response = restTemplate.exchange(DOGS_SERVICE_BASE_URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<DogModel>>() {});

            return response.getBody();
        }
        catch(HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public DogModel addDog(String locationId,DogModel dogRequestModel) {
        try{
            String url = DOGS_SERVICE_BASE_URL + "/" + locationId + "/" + "dogs" ;
            log.debug("Dogs-Service URL is: " + url);
            DogModel dogResponseModel = restTemplate.postForObject(url, dogRequestModel, DogModel.class);
            return dogResponseModel;
        }
        catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public DogModel updateDog(String locationId, DogModel dogRequestModel, String dogId) {
        try {
            String url = DOGS_SERVICE_BASE_URL + "/" + locationId + "/" + "dogs" + "/" + dogId;
            log.debug("Dogs-Service URL is: " + url);
            restTemplate.put(url, dogRequestModel);
            return getDogById(locationId,dogId);
        }
        catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public void deleteDog(String locationId,String dogId) {
        try {
            String url = DOGS_SERVICE_BASE_URL + "/" + locationId + "/" + "dogs" + "/" + dogId;
            log.debug("Dogs-Service URL is: " + url);
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