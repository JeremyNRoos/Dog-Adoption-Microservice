package com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.volunteer;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class VolunteerModel {

 private String volunteerId;
 private String FName;
 private String LName;
}
