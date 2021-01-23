package bsoft.com.ambtsgrenzen.proces;

import bsoft.com.ambtsgrenzen.client.AmbtsgrenzenClient;
import bsoft.com.ambtsgrenzen.model.*;
import bsoft.com.ambtsgrenzen.model.Geometry;
import bsoft.com.ambtsgrenzen.repository.BestuurlijkGebiedRepository;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Transactional
@Service
public class LoadBestuurlijkeGrenzen {

    private BestuurlijkGebiedRepository bestuurlijkGebiedRepository = null;

    @Autowired
    public LoadBestuurlijkeGrenzen(BestuurlijkGebiedRepository bestuurlijkGebiedRepository) {
        this.bestuurlijkGebiedRepository = bestuurlijkGebiedRepository;
    }

    public int load() {
        int status = 0;
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
            log.info("            - links self: {}", link.getSelf());
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

            double[][][] coords = geometry.getCoordinates();
            double[][] lines;
            Coordinate[] geoCoords = new Coordinate[coords[0].length];
            for (int k = 0; k < coords.length; k++) {
                log.info("line[{}] size: {}", k, coords[k].length);
                lines = coords[k];
                for (int l = 0; l < lines.length; l++) {
                    //       log.info("lines[{}] size: {}", l, lines[l].length);
                    Coordinate c = new Coordinate(lines[l][0], lines[l][1]);
                    geoCoords[l] = c;
                }
            }

            GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 28992);
            CoordinateArraySequence cas = new CoordinateArraySequence(geoCoords);
            LinearRing linearRing = new LinearRing(cas, geometryFactory);
            log.info("linearRing closed: {}", linearRing.isClosed());

            // exterior ring is empty
            Coordinate[] geoCoordsExt = new Coordinate[0];
            CoordinateArraySequence casExt = new CoordinateArraySequence(geoCoords);
            LinearRing l1 = new LinearRing(casExt, geometryFactory);
            LinearRing[] linearRingExt = { l1 };

            Polygon polygon = new Polygon(linearRing, null, geometryFactory);

            log.info("Created polygon: {}", polygon.toText());
            bg.setGeometry(polygon);

            bestuurlijkGebiedRepository.save(bg);

        }
        return status;
    }
}
