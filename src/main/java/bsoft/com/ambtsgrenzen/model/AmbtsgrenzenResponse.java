package bsoft.com.ambtsgrenzen.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AmbtsgrenzenResponse {
    @JsonProperty("_embedded")
    private Ambtsgrenzen embedded;
    @JsonProperty("_links")
    private HalLinks links;

}
