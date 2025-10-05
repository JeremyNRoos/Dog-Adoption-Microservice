package com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Dog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DogRepositoryTest {

    @Autowired
    private DogRepository dogRepository;

    @BeforeEach
    public void setUp() {
        dogRepository.deleteAll();
    }

    @Test
    public void whenDogsExist_thenReturnAllDogs(){
        //arrange
        Dog dog1 = new Dog("naty", DogBreedEnum.BULLDOG, 2, new Kennel(KennelSizeEnum.LARGE), VaccinationStatusEnum.VACCINATED, AvailabilityStatusEnum.AVAILABLE);
        Dog dog2 = new Dog("mike", DogBreedEnum.BULLDOG, 2, new Kennel(KennelSizeEnum.LARGE), VaccinationStatusEnum.VACCINATED, AvailabilityStatusEnum.AVAILABLE);
        dogRepository.save(dog1);
        dogRepository.save(dog2);
        long afterSizeDB = dogRepository.count();


        //act
        List<Dog> dogs = dogRepository.findAll();

        //assert
        assertNotNull(dogs);
        assertNotEquals( 0, afterSizeDB);
        assertEquals(afterSizeDB, dogs.size());
    }

    @Test
    public void whenDogExists_thenReturnDogByDogId(){
        //arrange
        Dog dog1 = new Dog("naty", DogBreedEnum.BULLDOG, 2, new Kennel(KennelSizeEnum.LARGE), VaccinationStatusEnum.VACCINATED, AvailabilityStatusEnum.AVAILABLE);
        dog1.setDogIdentifier(new DogIdentifier());
        dogRepository.save(dog1);

        String id = dog1.getDogIdentifier().getDogId();


        //act
        Dog dog = dogRepository.findDogByDogIdentifier_DogId(id);

        // assert
        assertNotNull(dog);
        assertEquals(dog1.getName(), dog.getName());
        assertEquals(id, dog.getDogIdentifier().getDogId());
        assertEquals(dog1.getBreed(), dog.getBreed());
    }

    @Test
    public void whenDogDoesNotExist_thenReturnNull(){
        //arrange
        Dog result = dogRepository.findDogByDogIdentifier_DogId("non-existing-id");

        //assert
        assertNull(result);
    }

    @Test
    public void whenValidEntitySaved_thenPersistAndReturn() {
        //arrange
        Dog dog1 = new Dog("naty", DogBreedEnum.BULLDOG, 2, new Kennel(KennelSizeEnum.LARGE), VaccinationStatusEnum.VACCINATED, AvailabilityStatusEnum.AVAILABLE);
        dog1.setDogIdentifier(new DogIdentifier());
        //act
        Dog dog = dogRepository.save(dog1);
        //assert
        assertNotNull(dog);
        assertNotNull(dog.getDogIdentifier().getDogId());
        assertEquals("naty", dog.getName());
        assertEquals(VaccinationStatusEnum.VACCINATED, dog.getVaccinationStatus());
        assertEquals(DogBreedEnum.BULLDOG, dog.getBreed());
        assertEquals(AvailabilityStatusEnum.AVAILABLE, dog.getAvailabilityStatus());
        assertEquals(KennelSizeEnum.LARGE, dog.getDogKennel().getKennelSize());
    }

    @Test
    void saveMultipleDogs_thenFindAll() {
        Dog dog1 = new Dog("Luna", DogBreedEnum.POODLE, 1, new Kennel(KennelSizeEnum.SMALL), VaccinationStatusEnum.NOT_VACCINATED, AvailabilityStatusEnum.AVAILABLE);
        Dog dog2 = new Dog("Rocky", DogBreedEnum.BULLDOG, 3, new Kennel(KennelSizeEnum.MEDIUM), VaccinationStatusEnum.VACCINATED, AvailabilityStatusEnum.AVAILABLE);
        dogRepository.save(dog1);
        dogRepository.save(dog2);

        List<Dog> dogs = dogRepository.findAll();
        assertEquals(2, dogs.size());
    }

    @Test
    void updateDog_thenFieldsAreUpdated() {
        Dog dog = new Dog("Max", DogBreedEnum.BOXER, 4, new Kennel(KennelSizeEnum.LARGE), VaccinationStatusEnum.NOT_VACCINATED, AvailabilityStatusEnum.AVAILABLE);
        dog.setDogIdentifier(new DogIdentifier());
        dogRepository.save(dog);

        dog.setName("Maximus");
        dog.setVaccinationStatus(VaccinationStatusEnum.VACCINATED);
        Dog updated = dogRepository.save(dog);

        assertEquals("Maximus", updated.getName());
        assertEquals(VaccinationStatusEnum.VACCINATED, updated.getVaccinationStatus());
    }

    @Test
    void deleteDog_thenDogIsRemoved() {
        Dog dog = new Dog("Shadow", DogBreedEnum.AFGHAN_HOUND, 5, new Kennel(KennelSizeEnum.MEDIUM), VaccinationStatusEnum.VACCINATED, AvailabilityStatusEnum.AVAILABLE);
        dog.setDogIdentifier(new DogIdentifier());
        dogRepository.save(dog);

        String id = dog.getDogIdentifier().getDogId();
        dogRepository.delete(dog);

        assertNull(dogRepository.findDogByDogIdentifier_DogId(id));
    }

    @Test
    void saveDog_withNullKennel_shouldPersist() {
        Dog dog = new Dog("Milo", DogBreedEnum.BICHON_FRISÉ, 2, null, VaccinationStatusEnum.VACCINATED, AvailabilityStatusEnum.AVAILABLE);
        dog.setDogIdentifier(new DogIdentifier());
        Dog saved = dogRepository.save(dog);

        assertNotNull(saved.getDogIdentifier().getDogId());
        assertNull(saved.getDogKennel());
    }

    @Test
    void dogSetters_shouldUpdateFields() {
        Dog dog = new Dog();
        dog.setName("Buddy");
        dog.setBreed(DogBreedEnum.BOXER);
        dog.setAge(6);
        dog.setDogIdentifier(new DogIdentifier());
        dog.setDogKennel(new Kennel(KennelSizeEnum.SMALL));
        dog.setVaccinationStatus(VaccinationStatusEnum.NOT_VACCINATED);
        dog.setAvailabilityStatus(AvailabilityStatusEnum.ADOPTED);

        assertEquals("Buddy", dog.getName());
        assertEquals(DogBreedEnum.BOXER, dog.getBreed());
        assertEquals(6, dog.getAge());
        assertEquals(KennelSizeEnum.SMALL, dog.getDogKennel().getKennelSize());
        assertEquals(VaccinationStatusEnum.NOT_VACCINATED, dog.getVaccinationStatus());
    }

    @Test
    void dogIdentifier_ContainsId() {
        DogIdentifier id = new DogIdentifier("abc-123");
        assertNotNull(id.getDogId());
    }

    @Test
    void dog_withNullNameAndBreed_shouldStillPersist() {
        Dog dog = new Dog();
        dog.setDogIdentifier(new DogIdentifier());
        dog.setAge(1);
        dog.setDogKennel(new Kennel(KennelSizeEnum.LARGE));
        dog.setVaccinationStatus(VaccinationStatusEnum.VACCINATED);
        dog.setAvailabilityStatus(AvailabilityStatusEnum.AVAILABLE);
        Dog saved = dogRepository.save(dog);

        assertNull(saved.getName());
        assertNull(saved.getBreed());
        assertEquals(1, saved.getAge());
    }

    @Test
    void kennel_getterWorksIndependently() {
        Kennel kennel = new Kennel(KennelSizeEnum.MEDIUM);
        assertEquals(KennelSizeEnum.MEDIUM, kennel.getKennelSize());
    }

    @Test
    void dogIdentifier_defaultConstructorGeneratesUUID() {
        DogIdentifier id = new DogIdentifier();
        assertNotNull(id.getDogId());
        assertTrue(id.getDogId().matches("^[a-f0-9\\-]{36}$"));
    }

    @Test
    void dog_toStringDoesNotCrashWithNulls() {
        Dog dog = new Dog();
        dog.setDogIdentifier(new DogIdentifier());
        assertNotNull(dog.toString());
    }

}