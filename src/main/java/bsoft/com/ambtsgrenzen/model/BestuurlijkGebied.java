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
