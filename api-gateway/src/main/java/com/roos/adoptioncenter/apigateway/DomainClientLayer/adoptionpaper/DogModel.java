package com.roos.adoptioncenter.apigateway.DomainClientLayer.adoptionpaper;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DogModel {

    private String id;
    private String name;
    private Integer age;

    public DogModel(String id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
}
