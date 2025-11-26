package com.roos.adoptioncenter.apigateway.businesslayer.location;

import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.location.LocationServiceClient;
import com.roos.adoptioncenter.apigateway.presentationlayer.location.LocationRequestModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.location.LocationResponseModel;
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
class LocationServiceUnitTest {

    @Mock
    private LocationServiceClient locationServiceClient;

    @InjectMocks
    private LocationServiceImpl locationService;

    private LocationRequestModel requestModel;
    private LocationResponseModel responseModel;
    private String locationId;

    @BeforeEach
    void setUp() {
        locationId = UUID.randomUUID().toString();

        requestModel = LocationRequestModel.builder()
                .name("City Shelter")
                .shelterType(null)  // Or set a proper enum if you prefer
                .streetAddress("123 Main St")
                .city("Metropolis")
                .province("State")
                .country("Country")
                .postalCode("12345")
                .capacity(100)
                .availableSpace(50)
                .build();

        responseModel = LocationResponseModel.builder()
                .locationId(locationId)
                .name("City Shelter")
                .shelterType(null)  // Or set a proper enum if you prefer
                .address(null)      // Or mock address if needed
                .capacity(100)
                .availableSpace(50)
                .build();
    }

    @Test
    void whenGetLocationById_thenReturnLocation() {
        when(locationServiceClient.getLocationById(locationId)).thenReturn(responseModel);

        var result = locationService.getLocationById(locationId);

        assertNotNull(result);
        assertEquals(responseModel.getLocationId(), result.getLocationId());
        assertEquals(responseModel.getName(), result.getName());
    }

    @Test
    void whenGetAllLocations_thenReturnList() {
        when(locationServiceClient.getAllLocations()).thenReturn(List.of(responseModel));

        var result = locationService.getAllLocations();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(responseModel.getLocationId(), result.get(0).getLocationId());
    }

    @Test
    void whenGetAllLocationsIsEmpty_thenReturnEmptyList() {
        when(locationServiceClient.getAllLocations()).thenReturn(Collections.emptyList());

        var result = locationService.getAllLocations();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void whenCreateLocation_thenReturnCreatedLocation() {
        when(locationServiceClient.addLocation(requestModel)).thenReturn(responseModel);

        var result = locationService.addLocation(requestModel);

        assertNotNull(result);
        assertEquals(responseModel.getLocationId(), result.getLocationId());
        assertEquals(responseModel.getName(), result.getName());
    }

    @Test
    void whenUpdateLocation_thenReturnUpdatedLocation() {
        when(locationServiceClient.updateLocation(locationId, requestModel)).thenReturn(responseModel);

        var result = locationService.updateLocation(locationId, requestModel);

        assertNotNull(result);
        assertEquals(responseModel.getLocationId(), result.getLocationId());
    }

    @Test
    void whenDeleteLocation_thenVerifyClientCall() {
        doNothing().when(locationServiceClient).deleteLocation(locationId);

        locationService.deleteLocation(locationId);

        verify(locationServiceClient, times(1)).deleteLocation(locationId);
    }

    @Test
    void whenGetLocationByIdNotFound_thenThrowNotFoundException() {
        when(locationServiceClient.getLocationById(anyString())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            locationService.getLocationById("nonexistent-id");
        });
    }

    @Test
    void whenLocationServiceClientThrowsException_thenPropagate() {
        when(locationServiceClient.getLocationById(anyString())).thenThrow(new RuntimeException("Service failure"));

        assertThrows(RuntimeException.class, () -> {
            locationService.getLocationById("some-id");
        });
    }
}
