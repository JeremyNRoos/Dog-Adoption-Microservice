package com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Dog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DogRepository extends JpaRepository<Dog, Integer> {
    Dog findDogByDogIdentifier_DogId(String DogId);
    Dog findDogByLocationIdentifier_LocationIdAndDogIdentifier_DogId(String locationId, String dogId);

    List<Dog> findAllByLocationIdentifier_LocationId(String locationId);
    List<Dog> findAllByDogIdentifier_DogId(String dogId);
}
