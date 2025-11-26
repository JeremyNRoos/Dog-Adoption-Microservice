package com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Dog;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Embeddable
@Getter
public class Kennel {
    @Enumerated(EnumType.STRING )
    @Column(name = "kennel_size")
    private KennelSizeEnum kennelSize;
//    @Column(name = "occupency_status")
//    private OccupencyStatusEnum occupencyStatus;

    public Kennel(@NotNull KennelSizeEnum kennelSize) {
        this.kennelSize = kennelSize;
    }
    public Kennel(){
    }
}
