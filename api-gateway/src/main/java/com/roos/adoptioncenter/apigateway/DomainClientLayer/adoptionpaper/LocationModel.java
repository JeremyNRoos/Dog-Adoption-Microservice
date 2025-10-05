package com.roos.adoptioncenter.apigateway.DomainClientLayer.adoptionpaper;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationModel {

    private String id;
    private String name;
    private ShelterTypeEnum shelterType;

    public LocationModel(String id, String name, ShelterTypeEnum shelterType) {
        this.id = id;
        this.name = name;
        this.shelterType = shelterType;
    }
}
