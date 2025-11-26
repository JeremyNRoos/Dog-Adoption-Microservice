package com.roos.adoptioncenter.apigateway.DomainClientLayer.adoptionpaper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.HttpErrorInfo;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.InvalidInputException;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
import com.roos.adoptioncenter.apigateway.presentationlayer.adoptionPaper.AdoptionPaperRequestModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.adoptionPaper.AdoptionPaperResponseModel;
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
public class AdoptionPaperServiceClient {
    private final RestTemplate restTemplate;


    private final ObjectMapper mapper;

    private final String ADOPTIONPAPER_SERVICE_BASE_URL;

    public AdoptionPaperServiceClient(  RestTemplate restTemplate,
                                  ObjectMapper mapper,
                                  @Value("${app.adoptionpaper-service.host}") String adoptionpaperServiceHost,
                                  @Value("${app.adoptionpaper-service.port}") String adoptionpaperServicePort
    ) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;

        ADOPTIONPAPER_SERVICE_BASE_URL = "http://" + adoptionpaperServiceHost + ":" + adoptionpaperServicePort + "/api/v1/adopters";
    }

    public AdoptionPaperResponseModel getAdoptionById(String adopterId,String adoptionPaperId) {
        try {
            String url = ADOPTIONPAPER_SERVICE_BASE_URL + "/" + adopterId + "/" + "adoptionpapers/" + adoptionPaperId;
            log.debug("Adoption paperss-Service URL is: " + url);
            AdoptionPaperResponseModel adoptionPaperResponseModel = restTemplate.getForObject(url, AdoptionPaperResponseModel.class);
            return adoptionPaperResponseModel;
        }
        catch(HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public List<AdoptionPaperResponseModel> getAllAdoptioPapers(String adopterId) {
        try {
            String url = ADOPTIONPAPER_SERVICE_BASE_URL + "/" + adopterId + "/" + "adoptionpapers";

            log.debug("adoptionPapers-service URL is {}", url);

            ResponseEntity<List<AdoptionPaperResponseModel>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<AdoptionPaperResponseModel>>() {});

            return response.getBody();
        }
        catch(HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public AdoptionPaperResponseModel addAdoptionPaper(String adopterId,AdoptionPaperRequestModel adoptionPaperRequestModel) {
        try{
            String url = ADOPTIONPAPER_SERVICE_BASE_URL + "/" + adopterId + "/" + "adoptionpapers";
            log.debug("AdoptionPapers-Service URL is: " + url);
            return restTemplate.postForObject(url, adoptionPaperRequestModel, AdoptionPaperResponseModel.class);
        }
        catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public AdoptionPaperResponseModel updateAdoptionPaper(String adopterId,AdoptionPaperRequestModel adoptionPaperRequestModel, String adoptionPaperId) {
        try {
            String url = ADOPTIONPAPER_SERVICE_BASE_URL + "/" + adopterId + "/" + "adoptionpapers" + "/" + adoptionPaperId;
            log.debug("AdoptionPapers-Service URL is: " + url);
            restTemplate.put(url, adoptionPaperRequestModel);
            return getAdoptionById(adopterId,adoptionPaperId);
        }
        catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }



    public void deleteAdoptionPaper(String adopterId,String adoptionPaperId) {
        try {
            String url = ADOPTIONPAPER_SERVICE_BASE_URL + "/" + adopterId + "/" + "adoptionpapers" + "/" + adoptionPaperId;
            log.debug("AdoptionPapers-Service URL is: " + url);
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
