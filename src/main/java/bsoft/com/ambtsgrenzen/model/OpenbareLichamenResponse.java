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
public class OpenbareLichamenResponse {
    @JsonProperty("_embedded")
    private OpenbareLichamen embedded;

    @JsonProperty("_links")
    private HalLinks links;
}
