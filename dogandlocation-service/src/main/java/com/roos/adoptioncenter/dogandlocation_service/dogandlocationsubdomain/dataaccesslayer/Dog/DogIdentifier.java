package com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Dog;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.UUID;

@Getter
@Embeddable
public class DogIdentifier {
    @Column(name = "dog_id")
    private String dogId;
    public DogIdentifier() {
        this.dogId = UUID.randomUUID().toString();
    }

    public DogIdentifier(String dogId) {
        this.dogId = dogId;
    }
}
