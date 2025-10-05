package com.roos.adoptioncenter.adopter_service.dataaccesslayer;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Data
@NoArgsConstructor
@Table(name = "adopter")
public class Adopter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Embedded
    private AdopterIdentifier adopterIdentifier;

    @JsonProperty("f_name")
    public String fName;

    @JsonProperty("l_name")
    public String lName;

    @Embedded
    public AdopterAddress adopterAddress;
    @Embedded
    public AdopterPhoneNumber adopterPhoneNumber;

    @Column(name = "contact_method_preference")
    @Enumerated(EnumType.STRING)
    public AdopterContactMethodPreference contactMethodPreference;

    public Adopter(@NotNull String fName, @NotNull String lName, @NotNull AdopterAddress adopterAddress, @NotNull AdopterPhoneNumber adopterPhoneNumber,@NotNull AdopterContactMethodPreference adopterContactMethodPreference) {
        this.fName = fName;
        this.lName = lName;
        this.adopterAddress = adopterAddress;
        this.adopterPhoneNumber = adopterPhoneNumber;
        this.contactMethodPreference = adopterContactMethodPreference;
    }
}
