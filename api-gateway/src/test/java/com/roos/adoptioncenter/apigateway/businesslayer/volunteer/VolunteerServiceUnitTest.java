    package com.roos.adoptioncenter.apigateway.businesslayer.volunteer;
    
    import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
    import com.roos.adoptioncenter.apigateway.DomainClientLayer.volunteer.VolunteerServiceClient;
    import com.roos.adoptioncenter.apigateway.presentationlayer.volunteer.VolunteerRequestModel;
    import com.roos.adoptioncenter.apigateway.presentationlayer.volunteer.VolunteerResponseModel;
    import com.roos.adoptioncenter.apigateway.DomainClientLayer.volunteer.TitleEnum2;
    import com.roos.adoptioncenter.apigateway.DomainClientLayer.volunteer.VolunteerAddress;
    import com.roos.adoptioncenter.apigateway.DomainClientLayer.volunteer.VolunteerPhoneNumber;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.*;
    import org.mockito.junit.jupiter.MockitoExtension;
    
    import java.util.Collections;
    import java.util.List;
    import java.util.UUID;
    
    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.Mockito.*;
    
    @ExtendWith(MockitoExtension.class)
    class VolunteerServiceUnitTest {
    
        @Mock
        private VolunteerServiceClient volunteerServiceClient;
    
        @InjectMocks
        private VolunteerServiceImpl volunteerService;
    
        private VolunteerRequestModel requestModel;
        private VolunteerResponseModel responseModel;
        private String volunteerId;
    
        @BeforeEach
        void setUp() {
            volunteerId = UUID.randomUUID().toString();
    
            VolunteerAddress address = VolunteerAddress.builder()
                    .streetAddress("123 Main St")
                    .city("Metropolis")
                    .province("QC")
                    .postalCode("12345")
                    .build();
    
            VolunteerPhoneNumber phoneNumber = VolunteerPhoneNumber.builder()
                    .phoneNumber("555-1234")
                    .build();
    
            requestModel = VolunteerRequestModel.builder()
                    .fName("John")
                    .lName("Doe")
                    .email("john.doe@example.com")
                    .salary("50000")
                    .title(TitleEnum2.VET)
                    .volunteerAddress(address)
                    .volunteerPhoneNumber(phoneNumber)
                    .build();
    
            responseModel = VolunteerResponseModel.builder()
                    .volunteerId(volunteerId)
                    .fName("John")
                    .lName("Doe")
                    .email("john.doe@example.com")
                    .salary("50000")
                    .title(TitleEnum2.VET)
                    .volunteerAddress(address)
                    .volunteerPhoneNumber(phoneNumber)
                    .build();
        }
    
        @Test
        void whenGetVolunteerById_thenReturnVolunteer() {
            when(volunteerServiceClient.getVolunteerById(volunteerId)).thenReturn(responseModel);
    
            var result = volunteerService.getVolunteerById(volunteerId);
    
            assertNotNull(result);
            assertEquals(responseModel.getVolunteerId(), result.getVolunteerId());
            assertEquals(responseModel.getFName(), result.getFName());
            assertEquals(responseModel.getLName(), result.getLName());
        }
    
        @Test
        void whenGetAllVolunteers_thenReturnList() {
            when(volunteerServiceClient.getAllVolunteers()).thenReturn(List.of(responseModel));
    
            var result = volunteerService.getVolunteers();
    
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(responseModel.getVolunteerId(), result.get(0).getVolunteerId());
        }
    
        @Test
        void whenGetAllVolunteersIsEmpty_thenReturnEmptyList() {
            when(volunteerServiceClient.getAllVolunteers()).thenReturn(Collections.emptyList());
    
            var result = volunteerService.getVolunteers();
    
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    
        @Test
        void whenCreateVolunteer_thenReturnCreatedVolunteer() {
            when(volunteerServiceClient.addVolunteer(requestModel)).thenReturn(responseModel);
    
            var result = volunteerService.addVolunteer(requestModel);
    
            assertNotNull(result);
            assertEquals(responseModel.getVolunteerId(), result.getVolunteerId());
            assertEquals(responseModel.getFName(), result.getFName());
        }
    
        @Test
        void whenUpdateVolunteer_thenReturnUpdatedVolunteer() {
            when(volunteerServiceClient.updateVolunteer(requestModel, volunteerId)).thenReturn(responseModel);
    
            var result = volunteerService.updateVolunteer(requestModel, volunteerId);
    
            assertNotNull(result);
            assertEquals(responseModel.getVolunteerId(), result.getVolunteerId());
        }
    
        @Test
        void whenDeleteVolunteer_thenVerifyClientCall() {
            doNothing().when(volunteerServiceClient).deleteVolunteer(volunteerId);
    
            volunteerService.deleteVolunteer(volunteerId);
    
            verify(volunteerServiceClient, times(1)).deleteVolunteer(volunteerId);
        }
    
        @Test
        void whenGetVolunteerByIdNotFound_thenThrowNotFoundException() {
            when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(null);
    
            assertThrows(NotFoundException.class, () -> {
                volunteerService.getVolunteerById("nonexistent-id");
            });
        }
    
        @Test
        void whenVolunteerServiceClientThrowsException_thenPropagate() {
            when(volunteerServiceClient.getVolunteerById(anyString())).thenThrow(new RuntimeException("Service failure"));
    
            assertThrows(RuntimeException.class, () -> {
                volunteerService.getVolunteerById("some-id");
            });
        }
    }
