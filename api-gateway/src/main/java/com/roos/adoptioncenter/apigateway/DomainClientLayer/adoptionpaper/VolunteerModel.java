package com.roos.adoptioncenter.apigateway.DomainClientLayer.adoptionpaper;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class VolunteerModel {

 private String id;
 private String FName;
 private String LName;

  public VolunteerModel(String id, String FName, String LName) {
   this.id = id;
   this.FName = FName;
   this.LName = LName;
  }
}
