package com.roos.adoptioncenter.apigateway.presentationlayer.dog;


import com.roos.adoptioncenter.apigateway.DomainClientLayer.dog.AvailabilityStatusEnum;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.dog.DogBreedEnum;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.dog.Kennel;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.dog.VaccinationStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DogRequestModel {
    public String locationId;
    public String name;
    public DogBreedEnum breed;
    public Integer age;
    public Kennel dogKennel;
    public VaccinationStatusEnum vaccinationStatus;
    public AvailabilityStatusEnum availabilityStatus;
}
