package bsoft.com.ambtsgrenzen.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"type", "geometries"})
public class GeometryCollection extends Geometry {
    private final Geometry[] geometries;

    @JsonCreator
    public GeometryCollection(@JsonProperty("geometries") Geometry[] geometries) {
        super();
        this.geometries = geometries;
    }

    public Geometry[] getGeometries() {
        return geometries;
    }
}