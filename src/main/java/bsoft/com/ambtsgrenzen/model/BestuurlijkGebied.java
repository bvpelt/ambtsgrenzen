package bsoft.com.ambtsgrenzen.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BestuurlijkGebied {
    private String identificatie;
    @JsonProperty("_embedded")
    private BestuurlijkGebiedEmbedded embedded;
    @JsonProperty("_links")
    private SelfLink links;
    private Geometry geometrie;
    private String domein;
    private String type;
}
