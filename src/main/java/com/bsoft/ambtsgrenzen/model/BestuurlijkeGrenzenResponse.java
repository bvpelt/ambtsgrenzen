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
public class BestuurlijkeGrenzenResponse {
    @JsonProperty("_embedded")
    private BestuurlijkeGrenzen embedded;

    @JsonProperty("_links")
    private HalLinks links;

}
