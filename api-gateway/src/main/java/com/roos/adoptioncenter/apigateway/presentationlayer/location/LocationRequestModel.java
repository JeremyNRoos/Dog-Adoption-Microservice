package com.roos.adoptioncenter.apigateway.presentationlayer.location;


import com.roos.adoptioncenter.apigateway.DomainClientLayer.location.ShelterTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequestModel {
    public String name;
    public ShelterTypeEnum shelterType;
    public String streetAddress;
    public String city;
    public String province;
    public String country;
    public String postalCode;
    public Integer capacity;
    public Integer availableSpace;
//    public Kennel kennel;
}
