package com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Location;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Data
@NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Embedded
    private LocationIdentifier locationIdentifier;

    public String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "shelter_type")
    public ShelterTypeEnum shelterType;

    @Embedded
    private LocationAddress address;

    public Integer capacity;

    @Column(name = "available_space")
    public Integer availableSpace;

    public Location(@NotNull String name, @NotNull ShelterTypeEnum shelterTypeEnum, @NotNull LocationAddress locationAddress, @NotNull int capacity, @NotNull int availableSpace) {
        this.name = name;
        this.shelterType = shelterTypeEnum;
        this.address = locationAddress;
        this.capacity = capacity;
        this.availableSpace = availableSpace;
    }

//    public Kennel kennel;
}
