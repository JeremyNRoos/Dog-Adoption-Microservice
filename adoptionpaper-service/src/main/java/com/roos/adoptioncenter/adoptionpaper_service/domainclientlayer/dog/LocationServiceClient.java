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
public class LocationServiceClient {

    private final RestTemplate restTemplate;


    private final ObjectMapper mapper;

    private final String LOCATION_SERVICE_BASE_URL;

    public LocationServiceClient(RestTemplate restTemplate,
                                 ObjectMapper mapper,
                                 @Value("${app.dogandlocation-service.host}") String locationsServiceHost,
                                 @Value("${app.dogandlocation-service.port}") String locationsServicePort
    ) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;

        LOCATION_SERVICE_BASE_URL = "http://" + locationsServiceHost + ":" + locationsServicePort + "/api/v1/locations";
    }

    public List<LocationModel> getAllLocations() {
        try {
            log.debug("location-service URL is {}", LOCATION_SERVICE_BASE_URL);

            ResponseEntity<List<LocationModel>> response = restTemplate.exchange(LOCATION_SERVICE_BASE_URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<LocationModel>>() {});

            return response.getBody();
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public LocationModel getLocationById(String locationId) {
        try {
            String url = LOCATION_SERVICE_BASE_URL + "/" + locationId;
            log.debug("Location-Service URL is: " + url);
            LocationModel locationResponseModel = restTemplate.getForObject(url, LocationModel.class);
            return locationResponseModel;
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public LocationModel addLocation(LocationModel locationRequestModel) {
        try {
            String url = LOCATION_SERVICE_BASE_URL;
            log.debug("Location-Service URL is: " + url);
            LocationModel locationResponseModel = restTemplate.postForObject(url, locationRequestModel, LocationModel.class);
            return locationResponseModel;
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public LocationModel updateLocation(String locationId, LocationModel locationRequestModel) {
        try {
            String url = LOCATION_SERVICE_BASE_URL + "/" + locationId;
            log.debug("Location-Service URL is: " + url);
            restTemplate.put(url, locationRequestModel);
            return getLocationById(locationId);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }


    public void deleteLocation(String locationId) {
        try {
            String url = LOCATION_SERVICE_BASE_URL + "/" + locationId;
            log.debug("Location-Service URL is: " + url);
            restTemplate.delete(url);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
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

