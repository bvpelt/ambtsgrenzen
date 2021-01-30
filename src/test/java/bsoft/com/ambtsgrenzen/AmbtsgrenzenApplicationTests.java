package bsoft.com.ambtsgrenzen;

import bsoft.com.ambtsgrenzen.client.AmbtsgrenzenClient;
import bsoft.com.ambtsgrenzen.model.*;
import bsoft.com.ambtsgrenzen.repository.BestuurlijkGebiedRepository;
import bsoft.com.ambtsgrenzen.repository.OpenbaarLichaamRepository;
import bsoft.com.ambtsgrenzen.utils.GeometryToJTS;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.io.IOException;

@Slf4j
@SpringBootTest
class AmbtsgrenzenApplicationTests {

    private BestuurlijkGebiedRepository bestuurlijkGebiedRepository;
    private OpenbaarLichaamRepository openbaarLichaamRepository;

    @Autowired
    public AmbtsgrenzenApplicationTests(final BestuurlijkGebiedRepository bestuurlijkGebiedRepository, final OpenbaarLichaamRepository openbaarLichaamRepository)
    {
        this.bestuurlijkGebiedRepository = bestuurlijkGebiedRepository;
        this.openbaarLichaamRepository = openbaarLichaamRepository;
        log.info("AmbtsgrenzenApplicationTests bestuurlijkGebiedRepository: {},  openbaarLichaamRepository: {}", bestuurlijkGebiedRepository, openbaarLichaamRepository);
    }

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
            log.info("            -- openbaarlichaam - code: {} type: {} naam: {} link self: {}", openbaarLichaam.getCode(), openbaarLichaam.getType(), openbaarLichaam.getName(), openbaarLichaam.getLinks().getSelf().getHref());
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

            org.locationtech.jts.geom.Geometry geo = new GeometryToJTS().geometryToGeo(geometry);
            //Polygon polygon = new GeometryToJTS().geometryToPolygon(geometry);

            if (geo != null) {
                bg.setGeometry(geo);
            }
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
                log.info("            -- openbaarlichaam - code: {} type: {} naam: {} link self: {}", openbaarLichaam.getCode(), openbaarLichaam.getType(), openbaarLichaam.getName(), openbaarLichaam.getLinks().getSelf().getHref());
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
        int page = 39;
        String url = bestuurlijkeGrensUri + "&page=" + page;
        status = getNextPage(url);
        log.info("LoadBestuurlijkeGrenzen - load status: {} next: {}", status, next);
        while (status == 0 && next != null && next.length() > 0) {
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
                log.info("            -- openbaarlichaam - code: {} type: {} naam: {} link self: {}", openbaarLichaam.getCode(), openbaarLichaam.getType(), openbaarLichaam.getName(), openbaarLichaam.getLinks().getSelf().getHref());
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

                org.locationtech.jts.geom.Geometry geo = new GeometryToJTS().geometryToGeo(geometry);
                //Polygon polygon = new GeometryToJTS().geometryToPolygon(geometry);
                //log.info("Created polygon: {}", polygon.toText());
                if (geo != null) {
                    bg.setGeometry(geo);

                    //bestuurlijkGebiedRepository.save(bg);
                    // if (persistBestuurlijkGebied(bg) > 0L) {
                    //     status++;
                    // }
                }
            }

            next = response.getLinks().getNext() == null? "" : response.getLinks().getNext().getHref();
            if (next == "") {
                status = -1;
            }
            log.info("LoadBestuurlijkeGrenzen - found next: {}", next);
        }
        log.info("LoadBestuurlijkeGrenzen - end   getNextPage");
        return status;
    }


    @Test
    public void test04()
            throws IOException {

        String json1 = "{\"type\":\"Point\", \"coordinates\": [ 5.020295189, 52.025627264 ] }";
        String json2 = "{\"type\":\"Polygon\", \"coordinates\": [ [ [ 4.920473995, 52.51199197 ], [ 4.920918469, 52.511846438 ], [ 4.921632215, 52.511635283 ], [ 4.922294632, 52.511470433 ], [ 4.922600176, 52.511412716 ], [ 4.922752826, 52.511394709 ], " +
                "[ 4.922869918, 52.511376563 ], [ 4.923007224, 52.511367798 ], [ 4.923439551, 52.511335379 ], [ 4.92403563, 52.511293676 ], [ 4.924523985, 52.511249061 ], [ 4.924686732, 52.511237292 ], [ 4.92485969, 52.511222462 ], " +
                             "[ 4.933499277, 52.51152796 ], [ 4.933636485, 52.511551618 ], [ 4.933740609, 52.511566476 ], [ 4.93392049, 52.511587403 ], [ 4.934071987, 52.511605336 ], [ 4.934228152, 52.511629065 ], [ 4.93435125, 52.511641102 ], " +
                "[ 4.934526255, 52.511676469 ], [ 4.934656807, 52.511705709 ], [ 4.934730232, 52.511722157 ], [ 4.935121721, 52.511750098 ], [ 4.935123428, 52.511750311 ], [ 4.935423949, 52.511787476 ], [ 4.935744338, 52.511823692 ], [ 4.920473995, 52.51199197 ]]]}";
        String json3 = "{\"type\":\"MultiPolygon\", \"coordinates\": [ [ [ [ 4.896291159, 52.322420008 ], [ 4.896407232, 52.322321202 ], [ 4.897145627, 52.32189658 ], [ 4.898280097, 52.321644124 ], [ 4.899346526, 52.321550622 ], " +
                "[ 4.90913474, 52.318253359 ], [ 4.909812283, 52.318261389 ], [ 4.910113857, 52.31826496 ], [ 4.910114165, 52.318264962 ], [ 4.910194761, 52.318265916 ], [ 4.91020397, 52.318266034 ], [ 4.910204616, 52.318266045 ], [ 4.896291159, 52.322420008 ] ] ], " +
                " [ [ [ 4.943095538, 52.292475537 ], [ 4.943175128, 52.292383307 ], [ 4.943219957, 52.292331355 ], [ 4.943640956, 52.291843541 ], [ 4.944145993, 52.291258442 ], [ 4.946034025, 52.289070235 ], [ 4.946375344, 52.288674517 ], " +
                "[ 4.954984417, 52.278626022 ], [ 4.954992888, 52.278615375 ], [ 4.955029113, 52.278569858 ], [ 4.955063202, 52.27852703 ], [ 4.955083258, 52.27850183 ], [ 4.955129971, 52.278443131 ], [ 4.955239684, 52.278305272 ], " +
                "[ 4.955242491, 52.278305876 ], [ 4.955487832, 52.278361292 ], [ 4.955736145, 52.278418534 ], [ 4.955843181, 52.278446203 ], [ 4.955996291, 52.2784886 ], [ 4.956162764, 52.278535602 ], [ 4.956302479, 52.278575226 ], [ 4.943095538, 52.292475537 ] ] ] ]}";

        ObjectMapper mapper = new ObjectMapper();
        try {
            log.info("Using json: {}", json1);
            Point point = mapper.reader().forType(Geometry.class).readValue(json1);
            log.info("Geometry type: {} coordiantes: {}", point.getType(), point.getCoordinates());

            log.info("Using json: {}", json2);
            Polygon polygon = mapper.reader().forType(Geometry.class).readValue(json2);
            log.info("Geometry type: {} coordiantes: {}", polygon.getType(), polygon.getCoordinates());

            log.info("Using json: {}", json3);
            MultiPolygon multiPolygon = mapper.reader().forType(Geometry.class).readValue(json3);
            log.info("Geometry type: {} coordiantes: {}", multiPolygon.getType(), multiPolygon.getCoordinates());

        } catch (Exception e) {
            log.info("Got excpetion: {}", e);
        }
    }

    @Test
    public void test05() {
        bsoft.com.ambtsgrenzen.database.BestuurlijkGebied bestuurlijkGebied;
        String identificatie = "GM0345";
        bestuurlijkGebied = bestuurlijkGebiedRepository.findByIdentificatie(identificatie);
        Assert.notNull(bestuurlijkGebied, "Bestuurlijkgebied expected, not found");
        Assert.isTrue(identificatie.equals(bestuurlijkGebied.getIdentificatie()), "Bestuurlijkgebied not as expected");
        log.info("Bestuurlijkgebied id: {}, identificatie: {}, domein: {}, type: {} ", bestuurlijkGebied.getId(), bestuurlijkGebied.getIdentificatie(), bestuurlijkGebied.getDomein(), bestuurlijkGebied.getType());
    }

    @Test
    public void test06() {
        bsoft.com.ambtsgrenzen.database.BestuurlijkGebied bestuurlijkGebied;
        bsoft.com.ambtsgrenzen.database.OpenbaarLichaam openbaarLichaam;
        String identificatie = "GM0345";
        bestuurlijkGebied = bestuurlijkGebiedRepository.findByIdentificatie(identificatie);
        Assert.notNull(bestuurlijkGebied, "Bestuurlijkgebied expected, not found");
        Assert.isTrue(identificatie.equals(bestuurlijkGebied.getIdentificatie()), "Bestuurlijkgebied not as expected");
        log.info("Bestuurlijkgebied id: {}, identificatie: {}, domein: {}, type: {} ", bestuurlijkGebied.getId(), bestuurlijkGebied.getIdentificatie(), bestuurlijkGebied.getDomein(), bestuurlijkGebied.getType());

        openbaarLichaam = openbaarLichaamRepository.findByCode(bestuurlijkGebied.getIdentificatie());
        Assert.notNull(openbaarLichaam, "Bestuurlijkgebied expected, not found");
        Assert.isTrue(bestuurlijkGebied.getIdentificatie().equals(openbaarLichaam.getCode()), "OpenbaarLichaam not as expected");
        log.info("OpenbaarLichaam id: {}, code: {}, oin: {}, type: {}, name: {}, bestuurslaag: {}, beginGeldigheid: {} ", openbaarLichaam.getId(), openbaarLichaam.getCode(), openbaarLichaam.getOin(), openbaarLichaam.getType(), openbaarLichaam.getName(), openbaarLichaam.getBestuurslaag(), openbaarLichaam.getBeginGeldigheid());
    }

    @Test
    public void test07() {
        bsoft.com.ambtsgrenzen.database.BestuurlijkGebied bestuurlijkGebied;
        bsoft.com.ambtsgrenzen.database.BestuurlijkGebied bestuurlijkGebiedCopie;
        bsoft.com.ambtsgrenzen.database.OpenbaarLichaam openbaarLichaam;
        String identificatie = "GM0345";
        bestuurlijkGebied = bestuurlijkGebiedRepository.findByIdentificatie(identificatie);
        bestuurlijkGebiedCopie = new bsoft.com.ambtsgrenzen.database.BestuurlijkGebied();
        bestuurlijkGebiedCopie.setId(bestuurlijkGebied.getId());
        bestuurlijkGebiedCopie.setIdentificatie(bestuurlijkGebied.getIdentificatie());
        bestuurlijkGebiedCopie.setOpenbaarLichaam(bestuurlijkGebied.getOpenbaarLichaam());
        bestuurlijkGebiedCopie.setGeometry(bestuurlijkGebied.getGeometry());
        bestuurlijkGebiedCopie.setDomein(bestuurlijkGebied.getDomein());
        bestuurlijkGebiedCopie.setType(bestuurlijkGebied.getType());


        Assert.isTrue(bestuurlijkGebiedCopie.equals(bestuurlijkGebied), "Bestuurlijkgebied copie not equal to bestuurlijkgebied");
        Assert.isTrue(bestuurlijkGebied.equals(bestuurlijkGebiedCopie), "Bestuurlijkgebied not equal to bestuurlijkgebied copie");
        log.info("Bestuurlijkgebied and copie are equal id: {}, identificatie: {}, domein: {}, type: {} ", bestuurlijkGebied.getId(), bestuurlijkGebied.getIdentificatie(), bestuurlijkGebied.getDomein(), bestuurlijkGebied.getType());

        bestuurlijkGebiedCopie.setId(null);
        Assert.isTrue(bestuurlijkGebiedCopie.equals(bestuurlijkGebied), "Bestuurlijkgebied copie not equal to bestuurlijkgebied");
        Assert.isTrue(bestuurlijkGebied.equals(bestuurlijkGebiedCopie), "Bestuurlijkgebied not equal to bestuurlijkgebied copie");
        log.info("Bestuurlijkgebied and copie (after id=null on copy) are equal id: {}, identificatie: {}, domein: {}, type: {} ", bestuurlijkGebied.getId(), bestuurlijkGebied.getIdentificatie(), bestuurlijkGebied.getDomein(), bestuurlijkGebied.getType());
        }
}
