package com.bsoft.ambtsgrenzen.utils;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

@Slf4j
@NoArgsConstructor

public class GeometryToJTS {

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 28992);

    public org.locationtech.jts.geom.Geometry geometryToGeo(com.bsoft.ambtsgrenzen.model.Geometry geometry) {
        org.locationtech.jts.geom.Geometry geo = null;

        if (geometry.getType().equals("Polygon")) {
            geo = geometryToPolygon((com.bsoft.ambtsgrenzen.model.Polygon) geometry);
        } else if (geometry.getType().equals("MultiPolygon")) {
            geo = geometryToMultiPolygon((com.bsoft.ambtsgrenzen.model.MultiPolygon) geometry);
        } else {
            log.error("Unsupported type: {}", geometry.getType());
        }

        return geo;
    }

    private Polygon geometryToPolygon(com.bsoft.ambtsgrenzen.model.Polygon geometry) {
        Polygon polygon = null;

        double[][][] coords = geometry.getCoordinates();
        polygon = getPolygon(coords);

        return polygon;
    }

    private MultiPolygon geometryToMultiPolygon(com.bsoft.ambtsgrenzen.model.MultiPolygon geometry) {
        MultiPolygon multiPolygon = null;
        double[][][][] coords = geometry.getCoordinates();
        int maxPolygons = coords.length;
        log.info("Aantal polygonen: {}", maxPolygons);

        Polygon[] polygons = new Polygon[maxPolygons];

        for (int k = 0; k < maxPolygons; k++) {
            polygons[k] = getPolygon(coords[k]);
        }

        multiPolygon = new MultiPolygon(polygons, geometryFactory);

        return multiPolygon;
    }

    private Polygon getPolygon(double[][][] coords) {
        Polygon polygon;
        int maxRings = coords.length;
        log.info("Number of rings: {}", maxRings);

        double[][] lines = null;
        LinearRing outline = null;
        LinearRing[] holes = null;

        // process outline and holes
        // outline is polygon 0 - all other polygons are holes
        if (maxRings > 1) {
            log.info("Holes found!");
            holes = new LinearRing[maxRings - 1];
        }

        for (int k = 0; k < maxRings; k++) {
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

        return polygon;
    }

}
