package bsoft.com.ambtsgrenzen.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MultiPolygon extends Geometry {
    private final double[][][][] coordinates;
    private final double[] bbox;

    @JsonCreator
    public MultiPolygon(@JsonProperty("coordinates") double [][][][] coordinates) {
        super();
        this.coordinates = coordinates;
        this.bbox = null;
    }

    public double[][][][] getCoordinates() {
        return coordinates;
    }

    public double[] getBbox() {
        return bbox;
    }
}
