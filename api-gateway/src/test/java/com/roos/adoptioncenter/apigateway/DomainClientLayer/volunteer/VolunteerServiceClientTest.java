package com.roos.adoptioncenter.apigateway.DomainClientLayer.volunteer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.HttpErrorInfo;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.InvalidInputException;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
import com.roos.adoptioncenter.apigateway.presentationlayer.volunteer.VolunteerRequestModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.volunteer.VolunteerResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VolunteerServiceClientTest {

    @InjectMocks
    private VolunteerServiceClient volunteerServiceClient;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private String host = "localhost";
    private String port = "8081";

    @BeforeEach
    void setup() {
        volunteerServiceClient = new VolunteerServiceClient(restTemplate, objectMapper, host, port);
    }

    @Test
    void testGetVolunteerById_Success() {
        String volunteerId = "v1";
        VolunteerResponseModel mockVolunteer = VolunteerResponseModel.builder()
                .volunteerId(volunteerId)
                .fName("John")
                .lName("Doe")
                .build();

        when(restTemplate.getForObject(anyString(), eq(VolunteerResponseModel.class))).thenReturn(mockVolunteer);

        VolunteerResponseModel result = volunteerServiceClient.getVolunteerById(volunteerId);

        assertNotNull(result);
        assertEquals(volunteerId, result.getVolunteerId());
        assertEquals("John", result.getFName());
    }

    @Test
    void testGetAllVolunteers_Success() {
        List<VolunteerResponseModel> volunteers = List.of(VolunteerResponseModel.builder().volunteerId("v1").fName("John").lName("Doe").build());
        ResponseEntity<List<VolunteerResponseModel>> response = new ResponseEntity<>(volunteers, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);

        List<VolunteerResponseModel> result = volunteerServiceClient.getAllVolunteers();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testAddVolunteer_Success() {
        VolunteerRequestModel request = VolunteerRequestModel.builder().fName("John").lName("Doe").build();
        VolunteerResponseModel response = VolunteerResponseModel.builder().volunteerId("v1").fName("John").lName("Doe").build();

        when(restTemplate.postForObject(anyString(), eq(request), eq(VolunteerResponseModel.class))).thenReturn(response);

        VolunteerResponseModel result = volunteerServiceClient.addVolunteer(request);

        assertNotNull(result);
    }

    @Test
    void testUpdateVolunteer_Success() {
        VolunteerRequestModel request = VolunteerRequestModel.builder().fName("John").lName("Doe").build();
        VolunteerResponseModel updated = VolunteerResponseModel.builder().volunteerId("v1").fName("John").lName("Doe").build();

        when(restTemplate.getForObject(anyString(), eq(VolunteerResponseModel.class))).thenReturn(updated);

        VolunteerResponseModel result = volunteerServiceClient.updateVolunteer(request, "v1");

        assertNotNull(result);
        assertEquals("John", result.getFName());
    }

    @Test
    void testDeleteVolunteer_Success() {
        doNothing().when(restTemplate).delete(anyString());
        assertDoesNotThrow(() -> volunteerServiceClient.deleteVolunteer("v1"));
    }



    @Test
    void testAddVolunteer_InvalidInput() throws Exception {
        VolunteerRequestModel request = VolunteerRequestModel.builder().fName("John").lName("Doe").build();
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid Input", null, null, null);
        when(restTemplate.postForObject(anyString(), eq(request), eq(VolunteerResponseModel.class))).thenThrow(ex);
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid input", "/volunteers"));

        assertThrows(InvalidInputException.class, () -> volunteerServiceClient.addVolunteer(request));
    }

    @Test
    void testDeleteVolunteer_NotFound() throws Exception {
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null);
        doThrow(ex).when(restTemplate).delete(anyString());
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.NOT_FOUND, "Not found", "/volunteers/v1"));

        assertThrows(NotFoundException.class, () -> volunteerServiceClient.deleteVolunteer("v1"));
    }

    @Test
    void testGetAllVolunteers_HttpClientError() throws Exception {
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error", null, null, null);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(ex);

        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () -> volunteerServiceClient.getAllVolunteers());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatusCode());
    }



    @Test
    void testGetAllVolunteers_EmptyList() {
        ResponseEntity<List<VolunteerResponseModel>> response = new ResponseEntity<>(List.of(), HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);

        List<VolunteerResponseModel> result = volunteerServiceClient.getAllVolunteers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


}
