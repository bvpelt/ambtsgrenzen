package bsoft.com.ambtsgrenzen;

import bsoft.com.ambtsgrenzen.client.AmbtsgrenzenClient;
import bsoft.com.ambtsgrenzen.model.Geometry;
import bsoft.com.ambtsgrenzen.model.*;
import bsoft.com.ambtsgrenzen.utils.GeometryToJTS;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.*;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class AmbtsgrenzenApplicationTests {

    private String next = "";

    @Test
    void contextLoads() {
        log.info("contextLoads...");
    }

    @Test
    void test01() {
        log.info("Start Test01");
        AmbtsgrenzenClient client = new AmbtsgrenzenClient();

        log.info("End   Test01 - Ready");
    }

    @Test
    void test02() {
        log.info("Start Test02");
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
        log.info("End   Test02");
    }

    @Test
    public void test03() {
        log.info("Start Test03");
        long status = 0;
        next = "";
        String bestuurlijkeGrensUri = "https://brk.basisregistraties.overheid.nl/api/bestuurlijke-grenzen/v2/bestuurlijke-gebieden?pageSize=10&type=territoriaal";

        log.info("LoadBestuurlijkeGrenzen - start load");
        int page = 1;
        String url = bestuurlijkeGrensUri + "&page=" + page;
        status = getNextPage(url);
        log.info("LoadBestuurlijkeGrenzen - load status: {} next: {}", status, next);
        while (next != null && next.length() > 0) {
            page++;
            url = bestuurlijkeGrensUri + "&page=" + page;
            status = getNextPage(url);
            log.info("LoadBestuurlijkeGrenzen - load status: {} next: {}", status, next);
        }
        log.info("LoadBestuurlijkeGrenzen - end   load");
        log.info("End   Test03");
    }


    private long getNextPage(String url) {
        log.info("LoadBestuurlijkeGrenzen - start getNextPage");
        AmbtsgrenzenClient client = new AmbtsgrenzenClient();
        long status = 0;

        AmbtsgrenzenResponse response = client.getBestuurlijkeGrens(url);
        if (response != null) {
            BestuurlijkGebied[] bestuurlijkGebied = response.getEmbedded().getBestuurlijkeGebieden();
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

                if (geometry.getType().equals("Polygon")) {
                    Polygon polygon = new GeometryToJTS().geometryToPolygon(geometry);
                    //log.info("Created polygon: {}", polygon.toText());
                    bg.setGeometry(polygon);
                } else {
                    log.warn("LoadBestuurlijkeGrenzen - found geometry type {}, not yet supported", geometry.getType());
                }

                //bestuurlijkGebiedRepository.save(bg);
               // if (persistBestuurlijkGebied(bg) > 0L) {
               //     status++;
               // }
            }

            next = response.getLinks().getNext().getHref();
            log.info("LoadBestuurlijkeGrenzen - found next: {}", next);
        }
        log.info("LoadBestuurlijkeGrenzen - end   getNextPage");
        return status;
    }
}
