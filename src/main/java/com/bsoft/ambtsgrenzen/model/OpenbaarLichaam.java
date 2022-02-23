package com.bsoft.ambtsgrenzen.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OpenbaarLichaam {
    private String code;

    private String oin;

    @JsonProperty("_links")
    private SelfLink links;

    private String type;

    @JsonProperty("naam")
    private String name;

    private String bestuurslaag;
}
