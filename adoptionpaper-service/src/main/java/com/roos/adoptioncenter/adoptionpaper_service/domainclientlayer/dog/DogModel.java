package com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.dog;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DogModel {

    private String dogId;
    private String name;
    private Integer age;
//    private String locationId;
}
