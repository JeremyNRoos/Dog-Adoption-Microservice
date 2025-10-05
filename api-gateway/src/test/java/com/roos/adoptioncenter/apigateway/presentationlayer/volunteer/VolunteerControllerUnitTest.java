package com.roos.adoptioncenter.apigateway.presentationlayer.volunteer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roos.adoptioncenter.apigateway.businesslayer.volunteer.VolunteerService;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.InvalidInputException;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.volunteer.TitleEnum2;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.volunteer.VolunteerAddress;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.volunteer.VolunteerPhoneNumber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
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
class VolunteerControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VolunteerService volunteerService;

    private VolunteerRequestModel volunteerRequest;
    private VolunteerResponseModel volunteerResponse;

    @BeforeEach
    void setUp() {
        volunteerRequest = VolunteerRequestModel.builder()
                .fName("John")
                .lName("Doe")
                .email("john.doe@example.com")
                .salary("50000")
                .title(TitleEnum2.MANAGER)
                .volunteerAddress(new VolunteerAddress("123 Street", "City", "Province", "Postal", "Country"))
                .volunteerPhoneNumber(new VolunteerPhoneNumber("123-456-7890"))
                .build();

        volunteerResponse = VolunteerResponseModel.builder()
                .volunteerId("volunteer-1")
                .fName("John")
                .lName("Doe")
                .email("john.doe@example.com")
                .salary("50000")
                .title(TitleEnum2.VET)
                .volunteerAddress(new VolunteerAddress("123 Street", "City", "Province", "Postal", "Country"))
                .volunteerPhoneNumber(new VolunteerPhoneNumber("123-456-7890"))
                .build();
    }

    @Test
    void testGetVolunteerById_Success() throws Exception {
        when(volunteerService.getVolunteerById("volunteer-1")).thenReturn(volunteerResponse);

        mockMvc.perform(get("/api/v1/volunteers/volunteer-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.volunteerId").value("volunteer-1"));
    }

    @Test
    void testGetVolunteerById_NotFound() throws Exception {
        when(volunteerService.getVolunteerById("volunteer-999")).thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/volunteers/volunteer-999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllVolunteers_Success() throws Exception {
        when(volunteerService.getVolunteers()).thenReturn(List.of(volunteerResponse));

        mockMvc.perform(get("/api/v1/volunteers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].volunteerId").value("volunteer-1"));
    }

    @Test
    void testAddVolunteer_Success() throws Exception {
        when(volunteerService.addVolunteer(any(VolunteerRequestModel.class))).thenReturn(volunteerResponse);

        mockMvc.perform(post("/api/v1/volunteers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(volunteerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.volunteerId").value("volunteer-1"));
    }

    @Test
    void testAddVolunteer_InvalidInput() throws Exception {
        when(volunteerService.addVolunteer(any(VolunteerRequestModel.class)))
                .thenThrow(new InvalidInputException("Invalid input"));

        mockMvc.perform(post("/api/v1/volunteers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(volunteerRequest)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testUpdateVolunteer_Success() throws Exception {
        when(volunteerService.updateVolunteer(any(VolunteerRequestModel.class), anyString())).thenReturn(volunteerResponse);

        mockMvc.perform(put("/api/v1/volunteers/volunteer-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(volunteerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.volunteerId").value("volunteer-1"));
    }

    @Test
    void testDeleteVolunteer_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/volunteers/volunteer-1"))
                .andExpect(status().isNoContent());
    }
}
