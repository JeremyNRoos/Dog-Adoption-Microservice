package com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Location;

import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Dog.DogIdentifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class LocationRepositoryTest {

    @Autowired
    private LocationRepository locationRepository;

    @BeforeEach
    public void setUp() {
        locationRepository.deleteAll();
    }

    @Test
    public void whenLocationsExist_thenReturnAllLocations(){
        //arrange
        Location location1 = new Location("apple bees", ShelterTypeEnum.SHELTER, new LocationAddress("123 street", "montreal", "qeuebec", "canada", "H9C 1A1"), 10, 12);
        Location location2 = new Location("apple bees", ShelterTypeEnum.SHELTER, new LocationAddress("123 street", "montreal", "qeuebec", "canada", "H9C 1A1"), 10, 12);
        locationRepository.save(location1);
        locationRepository.save(location2);
        long afterSizeDB = locationRepository.count();

        //act
        List<Location> locations = locationRepository.findAll();

        //assert
        assertNotNull(locations);
        assertNotEquals( 0, afterSizeDB);
        assertEquals(afterSizeDB, locations.size());
    }

    @Test
    public void whenLocationExists_thenReturnLocationById(){
        //arrange
        Location location1 = new Location("apple bees", ShelterTypeEnum.SHELTER, new LocationAddress("123 street", "montreal", "qeuebec", "canada", "H9C 1A1"), 10, 12);
        location1.setLocationIdentifier(new LocationIdentifier());
        locationRepository.save(location1);

        String id = location1.getLocationIdentifier().getLocationId();

        //act
        Location location = locationRepository.findLocationByLocationIdentifier_LocationId(id);

        //assert
        assertNotNull(location);
        assertEquals(id, location.getLocationIdentifier().getLocationId());
        assertEquals(location1.getName(), location.getName());
    }

    @Test
    public void whenLocationDoesNotExist_thenReturnNull(){
        //arrange
        Location result = locationRepository.findLocationByLocationIdentifier_LocationId("non-existing-id");

        //assert
        assertNull(result);
    }

    @Test
    public void whenValidEntitySaved_thenPersistAndReturn(){
        //arrange
        Location location = new Location("apple bees", ShelterTypeEnum.SHELTER, new LocationAddress("123 street", "montreal", "qeuebec", "canada", "H9C 1A1"), 10, 12);
        location.setLocationIdentifier(new LocationIdentifier());


        //act
        Location savedLocation = locationRepository.save(location);

        //assert
        assertNotNull(savedLocation);
        assertNotNull(savedLocation.getLocationIdentifier().getLocationId());
        assertEquals(location.getName(), savedLocation.getName());
        assertEquals(location.getAddress(), savedLocation.getAddress());
        assertEquals(location.getShelterType(), savedLocation.getShelterType());
        assertEquals(location.getCapacity(), savedLocation.getCapacity());
        assertEquals(location.getAvailableSpace(), savedLocation.getAvailableSpace());
    }
    @Test
    void deleteLocation_thenItIsRemoved() {
        Location location = new Location("Test Shelter", ShelterTypeEnum.SHELTER,
                new LocationAddress("321 Ave", "City", "Prov", "Country", "A1A1A1"), 15, 10);
        location.setLocationIdentifier(new LocationIdentifier());
        locationRepository.save(location);

        String id = location.getLocationIdentifier().getLocationId();
        locationRepository.delete(location);

        assertNull(locationRepository.findLocationByLocationIdentifier_LocationId(id));
    }

    @Test
    void deleteAllLocations_thenRepositoryIsEmpty() {
        locationRepository.save(new Location("Shelter A", ShelterTypeEnum.SHELTER,
                new LocationAddress("1 Road", "City", "Prov", "Country", "Z9Z9Z9"), 5, 5));
        assertTrue(locationRepository.count() > 0);
        locationRepository.deleteAll();
        assertEquals(0, locationRepository.count());
    }

    @Test
    void updateLocation_thenPersistChanges() {
        Location location = new Location("Old Name", ShelterTypeEnum.SHELTER,
                new LocationAddress("1 St", "City", "Prov", "Country", "123ABC"), 10, 5);
        location.setLocationIdentifier(new LocationIdentifier());
        locationRepository.save(location);

        location.setName("New Name");
        location.setCapacity(25);
        Location updated = locationRepository.save(location);

        assertEquals("New Name", updated.getName());
        assertEquals(25, updated.getCapacity());
    }

    @Test
    void locationAddress_fieldsMatchCorrectly() {
        LocationAddress address = new LocationAddress("101 Blvd", "CityX", "ProvY", "CountryZ", "Z1Z 2Z2");
        assertEquals("101 Blvd", address.getStreetAddress());
        assertEquals("CityX", address.getCity());
        assertEquals("ProvY", address.getProvince());
        assertEquals("CountryZ", address.getCountry());
        assertEquals("Z1Z 2Z2", address.getPostalCode());
    }

    @Test
    void location_settersUpdateCorrectly() {
        Location location = new Location();
        location.setName("Updated Shelter");
        location.setShelterType(ShelterTypeEnum.SHELTER);
        location.setCapacity(50);
        location.setAvailableSpace(20);
        location.setLocationIdentifier(new LocationIdentifier());
        location.setAddress(new LocationAddress("789 St", "Metro", "QC", "Canada", "H2H 2H2"));

        assertEquals("Updated Shelter", location.getName());
        assertEquals(50, location.getCapacity());
        assertEquals("Metro", location.getAddress().getCity());
    }

    @Test
    void locationAddress_toString_doesNotCrash() {
        LocationAddress address = new LocationAddress("456 Lane", "Place", "Area", "Country", "Y1Y1Y1");
        assertNotNull(address.toString());
    }

    @Test
    void locationIdentifier_defaultConstructorGeneratesUUID() {
        LocationIdentifier identifier = new LocationIdentifier();
        assertNotNull(identifier.getLocationId());
        assertTrue(identifier.getLocationId().matches("^[a-f0-9\\-]{36}$"));
    }

    @Test
    void locationIdentifier_parameterConstructorStoresId() {
        String id = "abc-123-id";
        LocationIdentifier identifier = new LocationIdentifier(id);
        assertEquals(id, identifier.getLocationId());
    }

    @Test
    void saveLocation_withNullAvailableSpace_shouldWork() {
        Location location = new Location("Test", ShelterTypeEnum.SHELTER,
                new LocationAddress("1 A", "City", "Prov", "Ctry", "X0X0X0"), 10, 0);
        location.setLocationIdentifier(new LocationIdentifier());
        Location saved = locationRepository.save(location);
        assertEquals(0, saved.getAvailableSpace());
    }

    @Test
    void saveLocation_withZeroCapacity_shouldWork() {
        Location location = new Location("ZeroCap", ShelterTypeEnum.SHELTER,
                new LocationAddress("1 B", "Town", "QC", "Canada", "Y0Y 0Y0"), 0, 0);
        location.setLocationIdentifier(new LocationIdentifier());
        Location saved = locationRepository.save(location);
        assertEquals(0, saved.getCapacity());
    }

    @Test
    void saveLocation_withLongName_shouldPersist() {
        String longName = "Shelter Name With Many Words And Characters To Test Field Lengths";
        Location location = new Location(longName, ShelterTypeEnum.SHELTER,
                new LocationAddress("321 X", "Montreal", "QC", "Canada", "H0H0H0"), 20, 5);
        location.setLocationIdentifier(new LocationIdentifier());
        Location saved = locationRepository.save(location);
        assertEquals(longName, saved.getName());
    }

    @Test
    void locationAddress_individualGettersWork() {
        LocationAddress address = new LocationAddress("123", "Ville", "Province", "Nation", "ABC123");
        assertEquals("123", address.getStreetAddress());
        assertEquals("Ville", address.getCity());
        assertEquals("Province", address.getProvince());
        assertEquals("Nation", address.getCountry());
        assertEquals("ABC123", address.getPostalCode());
    }

    @Test
    void locationToString_shouldNotBeNull() {
        Location location = new Location("ToString Shelter", ShelterTypeEnum.SHELTER,
                new LocationAddress("Road", "Urban", "ON", "Canada", "G5G 5G5"), 10, 5);
        location.setLocationIdentifier(new LocationIdentifier());
        assertNotNull(location.toString());
    }

    @Test
    void locationAddress_withEmptyFields_shouldStillWork() {
        LocationAddress address = new LocationAddress("", "", "", "", "");
        assertEquals("", address.getCity());
        assertEquals("", address.getPostalCode());
    }

    @Test
    void location_withNullValues_shouldNotBreakToString() {
        Location location = new Location();
        location.setLocationIdentifier(new LocationIdentifier());
        assertNotNull(location.toString());
    }
}