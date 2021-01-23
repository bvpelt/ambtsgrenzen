package bsoft.com.ambtsgrenzen;

import bsoft.com.ambtsgrenzen.client.AmbtsgrenzenClient;
import bsoft.com.ambtsgrenzen.model.Geometry;
import bsoft.com.ambtsgrenzen.model.*;
import bsoft.com.ambtsgrenzen.utils.GeometryToJTS;
import liquibase.pro.packaged.L;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
//@DataJpaTest
//@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class AmbtsgrenzenApplicationTests {


    @Test
    void contextLoads() {
        log.info("contextLoads...");
    }


    @Test
    void test01() {
        log.info("Test01");
        AmbtsgrenzenClient client = new AmbtsgrenzenClient();

        log.info("Test01 - Ready");
    }

    @Test
    void test02() {
        log.info("Test02");
        AmbtsgrenzenClient client = new AmbtsgrenzenClient();


        BestuurlijkGebied[] bestuurlijkGebied = client.getBestuurlijkeGrens().getEmbedded().getBestuurlijkeGebieden();
        log.info("Aantal bestuurlijke grenzen: {}", bestuurlijkGebied.length);

        int i = 0;
        int j = 0;
        for (i = 0; i < bestuurlijkGebied.length; i++) {
            j++;
            log.info("Element[{}] - identificatie: {}", i, bestuurlijkGebied[i].getIdentificatie());
            OpenbaarLichaam openbaarLichaam = bestuurlijkGebied[i].getEmbedded().getOpenbaarLichaam();
            log.info("            -- openbaarlichaam - code: {} type: {} naam: {} link self: {}", openbaarLichaam.getCode(), openbaarLichaam.getType(), openbaarLichaam.getNaam(), openbaarLichaam.getLinks().getSelf().getHref());
            MetaData metaData = bestuurlijkGebied[i].getEmbedded().getMetadata();
            log.info("            -- metadata - beginGeldigheid: {}", metaData.getBeginGeldigheid());
            SelfLink link = bestuurlijkGebied[i].getLinks();
            log.info("            - links self: {}", link.getSelf().getHref());
            log.info("            - geometrie");
            Geometry geometry = bestuurlijkGebied[i].getGeometrie();
            log.info("                type: {}", geometry.getType());
            // log.info("                coordinates: {}", geometry.getCoordinates());
            log.info("            - domein: {}", bestuurlijkGebied[i].getDomein());
            log.info("            - type: {}", bestuurlijkGebied[i].getType());

            bsoft.com.ambtsgrenzen.database.BestuurlijkGebied bg = new bsoft.com.ambtsgrenzen.database.BestuurlijkGebied();
            bg.setIdentificatie(bestuurlijkGebied[i].getIdentificatie());
            bg.setDomein(bestuurlijkGebied[i].getDomein());
            bg.setType(bestuurlijkGebied[i].getType());

            /*
            GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 28992);

            double[][][] coords = geometry.getCoordinates();
            log.info("Aantal polygonen: {}", coords.length);

            // for a polygon there is minimal the outlineCoordinates and 0..n holes
            // process outlineCoordinates
            double[][] lines = coords[0];
            Coordinate[] outlineCoordinates = new Coordinate[coords[0].length];
            log.info("number of coordinates in the outline: {}", lines.length);
            for (int l = 0; l < lines.length; l++) {
                //       log.info("lines[{}] size: {}", l, lines[l].length);
                Coordinate c = new Coordinate(lines[l][0], lines[l][1]);
                outlineCoordinates[l] = c;
            }
            CoordinateArraySequence cas = new CoordinateArraySequence(outlineCoordinates);
            LinearRing outline = new LinearRing(cas, geometryFactory);
            log.info("outline is closed: {}", outline.isClosed());

            // process holes
            LinearRing[] holes = null;
            if (coords.length > 1) {
                log.info("Holes found!");
                holes = new LinearRing[coords.length - 1];
                for (int k = 1; k < coords.length; k++) {
                    lines = coords[k];
                    log.info("number of coordinates in hole {}: {}", k - 1, lines.length);
                    for (int l = 0; l < lines.length; l++) {
                        //       log.info("lines[{}] size: {}", l, lines[l].length);
                        Coordinate c = new Coordinate(lines[l][0], lines[l][1]);
                        outlineCoordinates[l] = c;
                    }
                    CoordinateArraySequence casHole = new CoordinateArraySequence(outlineCoordinates);
                    LinearRing hole = new LinearRing(casHole, geometryFactory);
                    holes[k - 1] = hole;
                }
            }

            Polygon polygon = new Polygon(outline, holes, geometryFactory);

             */
            Polygon polygon = new GeometryToJTS().geometryToPolygon(geometry);
            bg.setGeometry(polygon);

        }

        HalLinks halLinks = client.getBestuurlijkeGrens().getLinks();
        log.info("next: {}", halLinks.getNext() != null ? halLinks.getNext().getHref() : "");
        String next = halLinks.getNext().getHref();
        log.info("self: {}", halLinks.getSelf() != null ? halLinks.getSelf().getHref() : "");
        log.info("prev: {}", halLinks.getPrev() != null ? halLinks.getPrev().getHref() : "");


        if (next.length() > 0) {
            bestuurlijkGebied = client.getBestuurlijkeGrens(next).getEmbedded().getBestuurlijkeGebieden();
            for (i = 0; i < bestuurlijkGebied.length; i++) {

                log.info("Element[{}] - identificatie: {}", j, bestuurlijkGebied[i].getIdentificatie());
                OpenbaarLichaam openbaarLichaam = bestuurlijkGebied[i].getEmbedded().getOpenbaarLichaam();
                log.info("            -- openbaarlichaam - code: {} type: {} naam: {} link self: {}", openbaarLichaam.getCode(), openbaarLichaam.getType(), openbaarLichaam.getNaam(), openbaarLichaam.getLinks().getSelf().getHref());
                MetaData metaData = bestuurlijkGebied[i].getEmbedded().getMetadata();
                log.info("            -- metadata - beginGeldigheid: {}", metaData.getBeginGeldigheid());
                SelfLink link = bestuurlijkGebied[i].getLinks();
                log.info("            - links self: {}", link.getSelf());
                log.info("            - geometrie");
                Geometry geometry = bestuurlijkGebied[i].getGeometrie();
                log.info("                type: {}", geometry.getType());
                // log.info("                coordinates: {}", geometry.getCoordinates());
                log.info("            - domein: {}", bestuurlijkGebied[i].getDomein());
                log.info("            - type: {}", bestuurlijkGebied[i].getType());
                j++;
            }
        }
        log.info("Test02 - Ready");
    }
}
