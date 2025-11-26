package com.roos.adoptioncenter.adoptionpaper_service.dataaccesslayer;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AdoptionPaperIdentifier {

    private String adoptionPaperId;

    public AdoptionPaperIdentifier() {
        this.adoptionPaperId = UUID.randomUUID().toString();
    }
    public AdoptionPaperIdentifier(String adoptionPaperId) {
        this.adoptionPaperId = adoptionPaperId;
    }
}
