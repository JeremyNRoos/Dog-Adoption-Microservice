package com.roos.adoptioncenter.apigateway.DomainClientLayer.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.HttpErrorInfo;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.InvalidInputException;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
import com.roos.adoptioncenter.apigateway.presentationlayer.adopter.AdopterRequestModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.adopter.AdopterResponseModel;

import com.roos.adoptioncenter.apigateway.presentationlayer.location.LocationRequestModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.location.LocationResponseModel;
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

    public List<LocationResponseModel> getAllLocations() {
        try {
            log.debug("location-service URL is {}", LOCATION_SERVICE_BASE_URL);

            ResponseEntity<List<LocationResponseModel>> response = restTemplate.exchange(LOCATION_SERVICE_BASE_URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<LocationResponseModel>>() {});

            return response.getBody();
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public LocationResponseModel getLocationById(String locationId) {
        try {
            String url = LOCATION_SERVICE_BASE_URL + "/" + locationId;
            log.debug("Location-Service URL is: " + url);
            LocationResponseModel locationResponseModel = restTemplate.getForObject(url, LocationResponseModel.class);
            return locationResponseModel;
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public LocationResponseModel addLocation(LocationRequestModel locationRequestModel) {
        try {
            String url = LOCATION_SERVICE_BASE_URL;
            log.debug("Location-Service URL is: " + url);
            LocationResponseModel locationResponseModel = restTemplate.postForObject(url, locationRequestModel, LocationResponseModel.class);
            return locationResponseModel;
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public LocationResponseModel updateLocation(String locationId, LocationRequestModel locationRequestModel) {
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

