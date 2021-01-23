package bsoft.com.ambtsgrenzen.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AmbtsgrenzenResponse {
    @JsonProperty("_embedded")
    private Ambtsgrenzen embedded;

    @JsonProperty("_links")
    private HalLinks links;

}
