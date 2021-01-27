package bsoft.com.ambtsgrenzen.model;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Point.class, name = "Point"),
        @JsonSubTypes.Type(value = Polygon.class, name = "Polygon"),
        @JsonSubTypes.Type(value = MultiPolygon.class, name = "MultiPolygon")
})
public abstract class Geometry {
    private String type;

    @JsonCreator
    public Geometry() {
        setType(getClass().getSimpleName());
    }
}

