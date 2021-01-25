package bsoft.com.ambtsgrenzen.utils;

import bsoft.com.ambtsgrenzen.model.*;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonSubTypes({
        @JsonSubTypes.Type(value = Point.class, name = "Point"),
        @JsonSubTypes.Type(value = LineString.class, name = "LineString"),
        @JsonSubTypes.Type(value = Polygon.class, name = "Polygon"),
        @JsonSubTypes.Type(value = MultiPoint.class, name = "MultiPoint"),
        @JsonSubTypes.Type(value = MultiLineString.class, name = "MultiLineString"),
        @JsonSubTypes.Type(value = MultiPolygon.class, name = "MultiPolygon"),
})

@JsonPropertyOrder({"type", "coordinates", "bbox"})
public class Geometry {
    private  String type;

    String getType() {
        return type;
    }
}
