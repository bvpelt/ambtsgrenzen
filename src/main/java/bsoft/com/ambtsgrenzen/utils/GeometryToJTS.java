package bsoft.com.ambtsgrenzen.utils;

import bsoft.com.ambtsgrenzen.model.Geometry;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.*;

@Slf4j
@NoArgsConstructor

public class GeometryToJTS {

    private GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 28992);

    public org.locationtech.jts.geom.Geometry geometryToPolygon(Geometry geometry) {
        org.locationtech.jts.geom.Geometry geo = null;


        /*
        double[][][] coords = geometry.getCoordinates();
        log.info("Aantal polygonen: {}", coords.length);
        double[][] lines = null;
        LinearRing outline = null;
        LinearRing[] holes = null;

        // process outline and holes
        // outline is polygon 0 - all other polygons are holes
        if (coords.length > 1) {
            log.info("Holes found!");
            holes = new LinearRing[coords.length - 1];
        }

        for (int k = 0; k < coords.length; k++) {
            lines = coords[k];
            Coordinate[] coordinates = new Coordinate[coords[k].length];
            log.info("number of coordinates in polygon {}: {}", k, lines.length);
            for (int l = 0; l < lines.length; l++) {
                //       log.info("lines[{}] size: {}", l, lines[l].length);
                Coordinate c = new Coordinate(lines[l][0], lines[l][1]);
                coordinates[l] = c;
            }
            CoordinateArraySequence casCoordinates = new CoordinateArraySequence(coordinates);
            LinearRing hole = new LinearRing(casCoordinates, geometryFactory);
            if (k == 0) {
                outline = hole;
            } else {
                holes[k - 1] = hole;
            }
        }

        polygon = new Polygon(outline, holes, geometryFactory);
*/
        return geo;
    }
}
