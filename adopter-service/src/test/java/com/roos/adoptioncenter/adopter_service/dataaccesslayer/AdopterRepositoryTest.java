package com.roos.adoptioncenter.adopter_service.dataaccesslayer;

import com.roos.adoptioncenter.adopter_service.utils.exceptions.InvalidInputException;
import com.roos.adoptioncenter.adopter_service.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AdopterRepositoryTest {

    @Autowired
    private AdopterRepository adopterRepository;

    @BeforeEach
    public void setUp() {
        adopterRepository.deleteAll();
    }

    @Test
    public void whenAdopterExist_thenReturnAllAdopters() {
        Adopter adopter1 = new Adopter("jeremy", "roos", new AdopterAddress("123 street", "montreal", "quebec", "canada", "J6N 5G9"), new AdopterPhoneNumber(PhoneTypeEnum.HOME, "579-931-3995"), AdopterContactMethodPreference.PHONE);
        Adopter adopter2 = new Adopter("jeremy", "roos", new AdopterAddress("123 street", "montreal", "quebec", "canada", "J6N 5G9"), new AdopterPhoneNumber(PhoneTypeEnum.HOME, "579-931-3995"), AdopterContactMethodPreference.PHONE);
        adopterRepository.save(adopter1);
        adopterRepository.save(adopter2);
        long afterSizeDB = adopterRepository.count();

        //act
        List<Adopter> adopters = adopterRepository.findAll();

        //assert
        assertNotNull(adopters);
        assertNotEquals(0, afterSizeDB);
        assertEquals(afterSizeDB, adopters.size());
    }

    @Test
    public void whenAdopterExist_thenReturnAdopterById() {
        Adopter adopter = new Adopter("jeremy", "roos", new AdopterAddress("123 street", "montreal", "quebec", "canada", "J6N 5G9"), new AdopterPhoneNumber(PhoneTypeEnum.HOME, "579-931-3995"), AdopterContactMethodPreference.PHONE);
        adopter.setAdopterIdentifier(new AdopterIdentifier());
        adopterRepository.save(adopter);

        String id = adopter.getAdopterIdentifier().getAdopterId();

        //act
        Adopter adopter2 = adopterRepository.findAdopterByAdopterIdentifier_AdopterId(id);

        //assert
        assertNotNull(adopter2);
        assertEquals(id, adopter2.getAdopterIdentifier().getAdopterId());
        assertEquals(adopter.getFName(), adopter2.getFName());
    }

    @Test
    public void whenAdopterDoesNotExist_thenReturnNull() {
        //arrange
        Adopter result = adopterRepository.findAdopterByAdopterIdentifier_AdopterId("non-existing-id");

        //assert
        assertNull(result);
    }

    @Test
    public void whenValidEntitySaved_thenPersistAndReturn() {
        //arrange
        Adopter adopter = new Adopter("jeremy", "roos", new AdopterAddress("123 street", "montreal", "quebec", "canada", "J6N 5G9"), new AdopterPhoneNumber(PhoneTypeEnum.HOME, "579-931-3995"), AdopterContactMethodPreference.PHONE);
        adopter.setAdopterIdentifier(new AdopterIdentifier());
        //act
        Adopter adopter2 = adopterRepository.save(adopter);
        //assert
        assertNotNull(adopter2);
        assertNotNull(adopter2.getAdopterIdentifier().getAdopterId());
        assertEquals(adopter.getFName(), adopter2.getFName());
        assertEquals(adopter.getLName(), adopter2.getLName());
        assertEquals(adopter.getAdopterAddress(), adopter2.getAdopterAddress());
        assertEquals(adopter.getAdopterPhoneNumber(), adopter2.getAdopterPhoneNumber());
        assertEquals(adopter.getContactMethodPreference(), adopter2.getContactMethodPreference());
    }

    @Test
    void saveMultipleAdopters_thenRetrieveAll() {
        Adopter adopter = new Adopter("David", "Lee", new AdopterAddress("123", "montreal", "quebec", "canada", "J6H7G4"), new AdopterPhoneNumber(PhoneTypeEnum.HOME, "438-521-3774"), AdopterContactMethodPreference.EMAIL);
        Adopter adopter2 = new Adopter("jeremy", "roos", new AdopterAddress("123", "montreal", "quebec", "canada", "J6H7G4"), new AdopterPhoneNumber(PhoneTypeEnum.HOME, "438-521-3774"), AdopterContactMethodPreference.EMAIL);
        adopterRepository.save(adopter);
        adopterRepository.save(adopter2);

        List<Adopter> adopters = adopterRepository.findAll();
        assertEquals(2, adopters.size());
    }

    @Test
    void saveAdopter_thenFindById() {
        Adopter adopter = new Adopter("David", "Lee", new AdopterAddress("123", "montreal", "quebec", "canada", "J6H7G4"), new AdopterPhoneNumber(PhoneTypeEnum.HOME, "438-521-3774"), AdopterContactMethodPreference.EMAIL);
        adopter.setAdopterIdentifier(new AdopterIdentifier());
        adopterRepository.save(adopter);

        String id = adopter.getAdopterIdentifier().getAdopterId();
        Adopter result = adopterRepository.findAdopterByAdopterIdentifier_AdopterId(id);

        assertNotNull(result);
        assertEquals("David", result.getFName());
        assertEquals("Lee", result.getLName());
    }

    @Test
    void updateAdopter_thenVerifyChanges() {
        Adopter adopter = new Adopter("David", "Lee", new AdopterAddress("123", "montreal", "quebec", "canada", "J6H7G4"), new AdopterPhoneNumber(PhoneTypeEnum.HOME, "438-521-3774"), AdopterContactMethodPreference.EMAIL);
        adopter.setAdopterIdentifier(new AdopterIdentifier());
        adopterRepository.save(adopter);

        adopter.setFName("Daniel");
        adopter.setLName("Chang");
        adopterRepository.save(adopter);

        Adopter updated = adopterRepository.findAdopterByAdopterIdentifier_AdopterId(adopter.getAdopterIdentifier().getAdopterId());
        assertEquals("Daniel", updated.getFName());
        assertEquals("Chang", updated.getLName());
    }

    @Test
    void deleteAdopter_thenShouldBeGone() {
        Adopter adopter = new Adopter("David", "Lee", new AdopterAddress("123", "montreal", "quebec", "canada", "J6H7G4"), new AdopterPhoneNumber(PhoneTypeEnum.HOME, "438-521-3774"), AdopterContactMethodPreference.EMAIL);
        adopter.setAdopterIdentifier(new AdopterIdentifier());
        adopterRepository.save(adopter);

        String id = adopter.getAdopterIdentifier().getAdopterId();
        adopterRepository.delete(adopter);

        assertNull(adopterRepository.findAdopterByAdopterIdentifier_AdopterId(id));
    }

    @Test
    void deleteAllAdopters_thenRepositoryEmpty() {
        Adopter adopter = new Adopter("David", "Lee", new AdopterAddress("123", "montreal", "quebec", "canada", "J6H7G4"), new AdopterPhoneNumber(PhoneTypeEnum.HOME, "438-521-3774"), AdopterContactMethodPreference.EMAIL);
        adopterRepository.save(adopter);
        assertTrue(adopterRepository.count() > 0);

        adopterRepository.deleteAll();
        assertEquals(0, adopterRepository.count());
    }

    @Test
    void saveAdopter_withNullContactPreference_shouldPersist() {
        Adopter adopter = new Adopter("David", "Lee", new AdopterAddress("123", "montreal", "quebec", "canada", "J6H7G4"), new AdopterPhoneNumber(PhoneTypeEnum.HOME, "438-521-3774"), AdopterContactMethodPreference.EMAIL);
        adopter.setAdopterIdentifier(new AdopterIdentifier());
        adopterRepository.save(adopter);

        Adopter result = adopterRepository.findAdopterByAdopterIdentifier_AdopterId(adopter.getAdopterIdentifier().getAdopterId());
        assertNotNull(result);
        assertNotNull(result.getContactMethodPreference());
    }

    @Test
    void adopterAddress_fieldsMatch() {
        AdopterAddress address = new AdopterAddress("456 Main", "Citytown", "QC", "Canada", "A1A1A1");

        assertEquals("456 Main", address.getStreetAddress());
        assertEquals("Citytown", address.getCity());
        assertEquals("QC", address.getProvince());
        assertEquals("Canada", address.getCountry());
        assertEquals("A1A1A1", address.getPostalCode());
    }

    @Test
    void adopterPhoneNumber_fieldsMatch() {
        AdopterPhoneNumber phone = new AdopterPhoneNumber(PhoneTypeEnum.MOBILE, "123-456-7890");

        assertEquals(PhoneTypeEnum.MOBILE, phone.getType());
        assertEquals("123-456-7890", phone.getPhoneNumber());
    }

    @Test
    void adopterIdentifier_generatesValidUUID() {
        AdopterIdentifier id = new AdopterIdentifier();
        assertNotNull(id.getAdopterId());
        assertTrue(id.getAdopterId().matches("^[a-f0-9\\-]{36}$"));
    }

    @Test
    void saveAdopter_thenToStringMethodsWork() {
        Adopter adopter = new Adopter("David", "Lee", new AdopterAddress("123", "montreal", "quebec", "canada", "J6H7G4"), new AdopterPhoneNumber(PhoneTypeEnum.HOME, "438-521-3774"), AdopterContactMethodPreference.EMAIL);
        adopter.setAdopterIdentifier(new AdopterIdentifier());

        assertNotNull(adopter.toString());
        assertNotNull(adopter.getAdopterIdentifier().toString());
        assertNotNull(adopter.getAdopterPhoneNumber().toString());
        assertNotNull(adopter.getAdopterAddress().toString());
    }

    @Test
    void adopter_setters_shouldUpdateFields() {
        Adopter adopter = new Adopter();
        adopter.setFName("Anna");
        adopter.setLName("Nguyen");
        adopter.setContactMethodPreference(AdopterContactMethodPreference.EMAIL);
        adopter.setAdopterIdentifier(new AdopterIdentifier());
        adopter.setAdopterAddress(new AdopterAddress("111", "City", "Prov", "Country", "Z9Z9Z9"));
        adopter.setAdopterPhoneNumber(new AdopterPhoneNumber(PhoneTypeEnum.WORK, "999-999-9999"));

        assertEquals("Anna", adopter.getFName());
        assertEquals("Nguyen", adopter.getLName());
        assertEquals(AdopterContactMethodPreference.EMAIL, adopter.getContactMethodPreference());
        assertEquals("City", adopter.getAdopterAddress().getCity());
        assertEquals("999-999-9999", adopter.getAdopterPhoneNumber().getPhoneNumber());
    }

    @Test
    void adopterAddress_constructor_shouldStoreAllValues() {
        AdopterAddress address = new AdopterAddress("22 Baker St", "Toronto", "ON", "Canada", "X1X1X1");
        assertEquals("Toronto", address.getCity());
        assertEquals("X1X1X1", address.getPostalCode());
    }

    @Test
    void adopterPhoneNumber_constructor_shouldStoreCorrectData() {
        AdopterPhoneNumber phone = new AdopterPhoneNumber(PhoneTypeEnum.MOBILE, "321-654-0987");
        assertEquals("321-654-0987", phone.getPhoneNumber());
        assertEquals(PhoneTypeEnum.MOBILE, phone.getType());
    }

    @Test
    void adopterIdentifier_parameterConstructor_setsIdProperly() {
        String expectedId = "d3fa09f3-8355-41d4-baad-d1a8ffb1f199";
        AdopterIdentifier identifier = new AdopterIdentifier(expectedId);
        assertEquals(expectedId, identifier.getAdopterId());
    }

    @Test
    void adopterIdentifier_defaultConstructor_producesValidUUID() {
        AdopterIdentifier identifier = new AdopterIdentifier();
        String uuid = identifier.getAdopterId();
        assertNotNull(uuid);
        assertTrue(uuid.matches("^[a-f0-9\\-]{36}$"));
    }





    @Test
    void saveAdopter_withDifferentPhoneTypes_shouldPersistCorrectly() {
        Adopter adopter = new Adopter("David", "Lee", new AdopterAddress("123", "montreal", "quebec", "canada", "J6H7G4"), new AdopterPhoneNumber(PhoneTypeEnum.HOME, "438-521-3774"), AdopterContactMethodPreference.EMAIL);
        adopter.setAdopterIdentifier(new AdopterIdentifier());
        Adopter saved = adopterRepository.save(adopter);

        assertEquals(PhoneTypeEnum.HOME, saved.getAdopterPhoneNumber().getType());
    }

    @Test
    void saveAdopter_withMinimalRequiredFields_shouldNotFail() {
        Adopter adopter = new Adopter();
        adopter.setFName("Bare");
        adopter.setLName("Minimum");
        adopter.setAdopterIdentifier(new AdopterIdentifier());
        adopter.setAdopterAddress(new AdopterAddress("123", "montreal", "quebec", "canada", "J6H7G4"));
        adopter.setAdopterPhoneNumber(new AdopterPhoneNumber(PhoneTypeEnum.HOME, "438-521-3774"));
        adopterRepository.save(adopter);

        List<Adopter> all = adopterRepository.findAll();
        assertTrue(all.stream().anyMatch(a -> "Bare".equals(a.getFName())));
    }

    @Test
    void adopterIdentifier_shouldContainId() {
        AdopterIdentifier id = new AdopterIdentifier("abc-123-id");
        assertNotNull(id.getAdopterId());
    }



    @Test
    void saveAdopter_withEmptyName_shouldStillPersist() {
        Adopter adopter = new Adopter();
        adopter.setAdopterIdentifier(new AdopterIdentifier());
        adopter.setFName("");
        adopter.setLName("");
        adopter.setAdopterAddress(new AdopterAddress("123", "city", "prov", "country", "Z1Z1Z1"));
        adopter.setAdopterPhoneNumber(new AdopterPhoneNumber(PhoneTypeEnum.HOME, "555-5555"));
        Adopter saved = adopterRepository.save(adopter);
        assertEquals("", saved.getFName());
        assertEquals("", saved.getLName());
    }

    @Test
    void saveAdopter_withNullName_shouldHandleGracefully() {
        Adopter adopter = new Adopter();
        adopter.setAdopterIdentifier(new AdopterIdentifier());
        adopter.setAdopterAddress(new AdopterAddress("123", "city", "prov", "country", "Z1Z1Z1"));
        adopter.setAdopterPhoneNumber(new AdopterPhoneNumber(PhoneTypeEnum.WORK, "555-0000"));
        adopter.setFName(null);
        adopter.setLName(null);
        Adopter saved = adopterRepository.save(adopter);
        assertNull(saved.getFName());
        assertNull(saved.getLName());
    }

    @Test
    void adopter_setters_workIndividually() {
        Adopter adopter = new Adopter();
        adopter.setFName("Test");
        adopter.setLName("Setter");
        adopter.setContactMethodPreference(null); // optional
        assertEquals("Test", adopter.getFName());
        assertEquals("Setter", adopter.getLName());
        assertNull(adopter.getContactMethodPreference());
    }

    @Test
    void adopterAddress_gettersWorkIndependently() {
        AdopterAddress address = new AdopterAddress("100", "City", "Prov", "Country", "000000");
        assertAll(
                () -> assertEquals("100", address.getStreetAddress()),
                () -> assertEquals("City", address.getCity()),
                () -> assertEquals("Prov", address.getProvince()),
                () -> assertEquals("Country", address.getCountry()),
                () -> assertEquals("000000", address.getPostalCode())
        );
    }

    @Test
    void adopterPhoneNumber_gettersWorkIndependently() {
        AdopterPhoneNumber phone = new AdopterPhoneNumber(PhoneTypeEnum.MOBILE, "800-0000");
        assertEquals(PhoneTypeEnum.MOBILE, phone.getType());
        assertEquals("800-0000", phone.getPhoneNumber());
    }

    @Test
    void adopter_withAllNullFields_shouldStillInstantiate() {
        Adopter adopter = new Adopter();
        assertNull(adopter.getFName());
        assertNull(adopter.getLName());
        assertNull(adopter.getAdopterPhoneNumber());
        assertNull(adopter.getAdopterAddress());
    }

    @Test
    void adopter_toStringShouldHandleNullsGracefully() {
        Adopter adopter = new Adopter();
        adopter.setAdopterIdentifier(new AdopterIdentifier());
        String str = adopter.toString();
        assertNotNull(str); // ensure .toString() doesn't crash
    }
}