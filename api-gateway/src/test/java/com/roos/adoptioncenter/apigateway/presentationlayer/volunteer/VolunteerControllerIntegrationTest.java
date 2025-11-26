package com.roos.adoptioncenter.apigateway.presentationlayer.volunteer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roos.adoptioncenter.apigateway.businesslayer.volunteer.VolunteerService;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.volunteer.TitleEnum2;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.volunteer.VolunteerAddress;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.volunteer.VolunteerPhoneNumber;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.InvalidInputException;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VolunteerController.class)
class VolunteerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VolunteerService volunteerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetVolunteerById_Success() throws Exception {
        VolunteerResponseModel response = VolunteerResponseModel.builder()
                .volunteerId("vol-123")
                .fName("John")
                .lName("Doe")
                .email("john.doe@example.com")
                .salary("50000")
                .title(TitleEnum2.MANAGER)
                .volunteerAddress(VolunteerAddress.builder()
                        .streetAddress("123 Street")
                        .city("City")
                        .province("Province")
                        .postalCode("12345")
                        .build())
                .volunteerPhoneNumber(VolunteerPhoneNumber.builder()
                        .phoneNumber("123-456-7890")
                        .build())
                .build();

        when(volunteerService.getVolunteerById("vol-123")).thenReturn(response);

        mockMvc.perform(get("/api/v1/volunteers/vol-123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.volunteerId").value("vol-123"));
    }

    @Test
    void testGetVolunteerById_NotFound() throws Exception {
        when(volunteerService.getVolunteerById("vol-999"))
                .thenThrow(new NotFoundException("Volunteer not found"));

        mockMvc.perform(get("/api/v1/volunteers/vol-999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllVolunteers() throws Exception {
        VolunteerResponseModel response = VolunteerResponseModel.builder()
                .volunteerId("vol-123")
                .fName("John")
                .lName("Doe")
                .email("john.doe@example.com")
                .salary("50000")
                .title(TitleEnum2.MANAGER)
                .volunteerAddress(VolunteerAddress.builder()
                        .streetAddress("123 Street")
                        .city("City")
                        .province("Province")
                        .postalCode("12345")
                        .build())
                .volunteerPhoneNumber(VolunteerPhoneNumber.builder()
                        .phoneNumber("123-456-7890")
                        .build())
                .build();

        when(volunteerService.getVolunteers()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/volunteers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].volunteerId").value("vol-123"));
    }

    @Test
    void testAddVolunteer() throws Exception {
        VolunteerRequestModel request = VolunteerRequestModel.builder()
                .fName("Jane")
                .lName("Smith")
                .email("jane.smith@example.com")
                .salary("45000")
                .title(TitleEnum2.VET)
                .volunteerAddress(VolunteerAddress.builder()
                        .streetAddress("456 Avenue")
                        .city("AnotherCity")
                        .province("AnotherProvince")
                        .postalCode("67890")
                        .build())
                .volunteerPhoneNumber(VolunteerPhoneNumber.builder()
                        .phoneNumber("987-654-3210")
                        .build())
                .build();

        VolunteerResponseModel response = VolunteerResponseModel.builder()
                .volunteerId("vol-456")
                .fName("Jane")
                .lName("Smith")
                .email("jane.smith@example.com")
                .salary("45000")
                .title(TitleEnum2.VET)
                .volunteerAddress(VolunteerAddress.builder()
                        .streetAddress("456 Avenue")
                        .city("AnotherCity")
                        .province("AnotherProvince")
                        .postalCode("67890")
                        .build())
                .volunteerPhoneNumber(VolunteerPhoneNumber.builder()
                        .phoneNumber("987-654-3210")
                        .build())
                .build();

        when(volunteerService.addVolunteer(any(VolunteerRequestModel.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/volunteers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.volunteerId").value("vol-456"));
    }

    @Test
    void testAddVolunteer_InvalidInput() throws Exception {
        VolunteerRequestModel request = VolunteerRequestModel.builder()
                .fName("Jane")
                .lName("Smith")
                .email("jane.smith@example.com")
                .salary("45000")
                .title(TitleEnum2.VET)
                .volunteerAddress(VolunteerAddress.builder()
                        .streetAddress("456 Avenue")
                        .city("AnotherCity")
                        .province("AnotherProvince")
                        .postalCode("67890")
                        .build())
                .volunteerPhoneNumber(VolunteerPhoneNumber.builder()
                        .phoneNumber("987-654-3210")
                        .build())
                .build();

        when(volunteerService.addVolunteer(any(VolunteerRequestModel.class)))
                .thenThrow(new InvalidInputException("Invalid input"));

        mockMvc.perform(post("/api/v1/volunteers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testUpdateVolunteer() throws Exception {
        VolunteerRequestModel request = VolunteerRequestModel.builder()
                .fName("John Updated")
                .lName("Doe Updated")
                .email("john.doe.updated@example.com")
                .salary("60000")
                .title(TitleEnum2.MANAGER)
                .volunteerAddress(VolunteerAddress.builder()
                        .streetAddress("Updated Street")
                        .city("Updated City")
                        .province("Updated Province")
                        .postalCode("99999")
                        .build())
                .volunteerPhoneNumber(VolunteerPhoneNumber.builder()
                        .phoneNumber("111-222-3333")
                        .build())
                .build();

        VolunteerResponseModel response = VolunteerResponseModel.builder()
                .volunteerId("vol-123")
                .fName("John Updated")
                .lName("Doe Updated")
                .email("john.doe.updated@example.com")
                .salary("60000")
                .title(TitleEnum2.MANAGER)
                .volunteerAddress(VolunteerAddress.builder()
                        .streetAddress("Updated Street")
                        .city("Updated City")
                        .province("Updated Province")
                        .postalCode("99999")
                        .build())
                .volunteerPhoneNumber(VolunteerPhoneNumber.builder()
                        .phoneNumber("111-222-3333")
                        .build())
                .build();

        when(volunteerService.updateVolunteer(any(VolunteerRequestModel.class), anyString())).thenReturn(response);

        mockMvc.perform(put("/api/v1/volunteers/vol-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.volunteerId").value("vol-123"));
    }

    @Test
    void testDeleteVolunteer() throws Exception {
        mockMvc.perform(delete("/api/v1/volunteers/vol-123"))
                .andExpect(status().isNoContent());
    }
}