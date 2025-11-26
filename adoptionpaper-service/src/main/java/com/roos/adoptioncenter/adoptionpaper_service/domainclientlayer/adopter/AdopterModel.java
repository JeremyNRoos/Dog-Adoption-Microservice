package com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.adopter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdopterModel {

    private String adopterId;
    private String FName;
    private String LName;


}
