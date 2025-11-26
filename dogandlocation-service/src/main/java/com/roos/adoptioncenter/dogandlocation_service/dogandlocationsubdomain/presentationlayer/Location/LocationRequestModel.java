package com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.presentationlayer.Location;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Location.ShelterTypeEnum;
import jakarta.persistence.Column;
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
