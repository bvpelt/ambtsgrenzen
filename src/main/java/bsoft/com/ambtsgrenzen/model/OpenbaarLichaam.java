package bsoft.com.ambtsgrenzen.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpenbaarLichaam {
    private String code;
    @JsonProperty("_links")
    private SelfLink links;
    private String type;
    private String naam;
}
