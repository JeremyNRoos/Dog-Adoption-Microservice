package com.roos.adoptioncenter.apigateway.presentationlayer.dog;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.dog.AvailabilityStatusEnum;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.dog.DogBreedEnum;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.dog.Kennel;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.dog.VaccinationStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DogResponseModel extends RepresentationModel<DogResponseModel> {
//    @JsonProperty("dogModel")
    public String dogId;
    public String locationId;
    public String name;
    public DogBreedEnum breed;
    public Integer age;
    public Kennel dogKennel;
    public VaccinationStatusEnum vaccinationStatus;
    public AvailabilityStatusEnum availabilityStatus;
}
