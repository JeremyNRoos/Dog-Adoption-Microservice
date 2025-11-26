package com.roos.adoptioncenter.dogandlocation_service;

import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Location.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DogandlocationServiceApplicationTests {

	@Autowired
	private LocationRepository locationRepository;

	@BeforeEach
	public void setUp() {
		locationRepository.deleteAll();
	}

	@Test
	public void whenLocationsExist_thenReturnAllLocations() {
		// arrange
		Location location1 = new Location("downtown brazil", ShelterTypeEnum.SHELTER, new LocationAddress("123 street", "montreal", "qeuebec", "canada", "H9C 1A1"), 1, 2);
		Location location2 = new Location("downtown place", ShelterTypeEnum.FOSTERHOME, new LocationAddress("124 street", "toronto", "province", "country", "H9C 2B4"), 1, 2);
		locationRepository.save(location1);
		locationRepository.save(location2);

		long count = locationRepository.count();

		// act
		List<Location> locations = locationRepository.findAll();

		// assert
		assertNotNull(locations);
		assertEquals(count, locations.size());
	}

	@Test
	public void whenLocationExists_thenReturnLocationByLocationId() {
		// arrange
		Location location = new Location("downtown brazil", ShelterTypeEnum.SHELTER, new LocationAddress("123 street", "montreal", "qeuebec", "canada", "H9C 1A1"), 1, 2);
		location.setLocationIdentifier(new LocationIdentifier());
		locationRepository.save(location);
		String locationId = location.getLocationIdentifier().getLocationId();

		// act
		Location result = locationRepository.findLocationByLocationIdentifier_LocationId(locationId);

		// assert
		assertNotNull(result);
		assertEquals("downtown brazil", result.getName());
		assertEquals(locationId, result.getLocationIdentifier().getLocationId());
		assertEquals(location.getShelterType(), result.getShelterType());
	}

	@Test
	public void whenLocationDoesNotExist_thenReturnNull() {
		// act
		Location result = locationRepository.findLocationByLocationIdentifier_LocationId("non-existent-id");

		// assert
		assertNull(result);
	}

	@Test
	public void whenValidLocationSaved_thenPersistAndReturn() {
		// arrange
		Location location = new Location("downtown brazil", ShelterTypeEnum.SHELTER, new LocationAddress("123 street", "montreal", "qeuebec", "canada", "H9C 1A1"), 1, 2);
		location.setLocationIdentifier(new LocationIdentifier());

		// act
		Location saved = locationRepository.save(location);

		// assert
		assertNotNull(saved);
		assertNotNull(saved.getLocationIdentifier().getLocationId());
		assertEquals("downtown brazil", saved.getName());
		assertEquals(ShelterTypeEnum.SHELTER, saved.getShelterType());
	}
}
