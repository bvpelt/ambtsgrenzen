package com.bsoft.ambtsgrenzen.proces;

import com.bsoft.ambtsgrenzen.client.AmbtsgrenzenClient;
import bsoft.com.ambtsgrenzen.model.*;
import com.bsoft.ambtsgrenzen.repository.BestuurlijkGebiedRepository;
import com.bsoft.ambtsgrenzen.repository.OpenbaarLichaamRepository;
import com.bsoft.ambtsgrenzen.utils.GeometryToJTS;
import com.bsoft.ambtsgrenzen.model.*;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@NoArgsConstructor
@Slf4j
@Service
public class LoadBestuurlijkeGrenzen {

    private final String scheme = "https";
    private final String host = "brk.basisregistraties.overheid.nl";
    private final String path = "/api/bestuurlijke-grenzen/v2";
    private final String bestuurlijkeGrensUri = scheme + "://" + host + path + "/bestuurlijke-gebieden?pageSize=10&type=territoriaal";
    private BestuurlijkGebiedRepository bestuurlijkGebiedRepository = null;
    private OpenbaarLichaamRepository openbaarLichaamRepository = null;
    private long status = 0L;
    private String next;
    private AmbtsgrenzenClient client;

    @Autowired
    public LoadBestuurlijkeGrenzen(final BestuurlijkGebiedRepository bestuurlijkGebiedRepository, final OpenbaarLichaamRepository openbaarLichaamRepository) {
        this.bestuurlijkGebiedRepository = bestuurlijkGebiedRepository;
        this.openbaarLichaamRepository = openbaarLichaamRepository;
        this.client = new AmbtsgrenzenClient();
    }

    public long load() {
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
        return status;
    }

    private long getNextPage(String url) {
        log.info("LoadBestuurlijkeGrenzen - start getNextPage");

        BestuurlijkeGrenzenResponse response = client.getBestuurlijkeGrens(url);
        if (response != null) {
            BestuurlijkGebied[] bestuurlijkGebied = response.getEmbedded().getBestuurlijkeGebieden();
            log.info("Aantal bestuurlijke grenzen: {}", bestuurlijkGebied.length);

            int i = 0;
            //int j = 0;
            for (i = 0; i < bestuurlijkGebied.length; i++) {
                //  j++;
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

                com.bsoft.ambtsgrenzen.database.BestuurlijkGebied bg = new com.bsoft.ambtsgrenzen.database.BestuurlijkGebied();
                bg.setIdentificatie(bestuurlijkGebied[i].getIdentificatie());
                bg.setDomein(bestuurlijkGebied[i].getDomein());
                bg.setType(bestuurlijkGebied[i].getType());

                com.bsoft.ambtsgrenzen.database.OpenbaarLichaam ol = new com.bsoft.ambtsgrenzen.database.OpenbaarLichaam();
                ol.setCode(openbaarLichaam.getCode());
                ol.setName(openbaarLichaam.getName());
                ol.setType(openbaarLichaam.getType());

                org.locationtech.jts.geom.Geometry geo = new GeometryToJTS().geometryToGeo(geometry);

                if (geo != null) {
                    //log.info("Created polygon: {}", polygon.toText());
                    bg.setGeometry(geo);

                    if (persistBestuurlijkGebied(bg, ol) > 0L) {
                        status++;
                    }
                }
            }

            next = response.getLinks().getNext() == null ? "" : response.getLinks().getNext().getHref();

            log.info("LoadBestuurlijkeGrenzen - found next: {}", next);
        } else {
            next = "";
        }
        log.info("LoadBestuurlijkeGrenzen - end   getNextPage");
        return status;
    }

    /*
    Check if openbaarlichaam already exists
    if not exists - save new entry
    if exists     - update fields, save found entry
    Check if bestuurlijkgebied already exists
    if not exists - save new entry
    if exists     - update fields, save found entry
     */
    @Transactional
    public long persistBestuurlijkGebied(com.bsoft.ambtsgrenzen.database.BestuurlijkGebied bestuurlijkGebied, com.bsoft.ambtsgrenzen.database.OpenbaarLichaam openbaarLichaam) {
        com.bsoft.ambtsgrenzen.database.OpenbaarLichaam ol = null;
        com.bsoft.ambtsgrenzen.database.BestuurlijkGebied bg = null;
        com.bsoft.ambtsgrenzen.database.BestuurlijkGebied result = null;

        Optional<com.bsoft.ambtsgrenzen.database.OpenbaarLichaam> existingOpenbaarLichaam = openbaarLichaamRepository.findByCode(openbaarLichaam.getCode());
        if (existingOpenbaarLichaam.isEmpty()) {
            ol = openbaarLichaamRepository.save(openbaarLichaam);
        } else {
            com.bsoft.ambtsgrenzen.database.OpenbaarLichaam exOl = existingOpenbaarLichaam.get();
            if (!openbaarLichaam.equals(exOl)) {
                exOl.setOin(openbaarLichaam.getOin());
                exOl.setType(openbaarLichaam.getType());
                exOl.setName(openbaarLichaam.getName());
                exOl.setBestuurslaag(openbaarLichaam.getBestuurslaag());
                exOl.setBeginGeldigheid(openbaarLichaam.getBeginGeldigheid());
            }
            ol = openbaarLichaamRepository.save(exOl);
        }

        Optional<com.bsoft.ambtsgrenzen.database.BestuurlijkGebied> existingBestuurlijkGebied = bestuurlijkGebiedRepository.findByIdentificatie(bestuurlijkGebied.getIdentificatie());
        if (existingBestuurlijkGebied.isEmpty()) {
            bestuurlijkGebied.setOpenbaarLichaam(ol);
            result = bestuurlijkGebiedRepository.save(bestuurlijkGebied);
            log.info("New bestuurlijkgebied id: {}, identificatie: {}", result.getId(), result.getIdentificatie());
        } else {
            com.bsoft.ambtsgrenzen.database.BestuurlijkGebied exBe = existingBestuurlijkGebied.get();
            if (!bestuurlijkGebied.equals(exBe)) {
                exBe.setGeometry(bestuurlijkGebied.getGeometry());
                exBe.setDomein(bestuurlijkGebied.getDomein());
                exBe.setType(bestuurlijkGebied.getType());
                exBe.setOpenbaarLichaam(ol);
                result = bestuurlijkGebiedRepository.save(exBe);
                log.info("Changed bestuurlijkgebied id: {}, identificatie: {}", exBe.getId(), exBe.getIdentificatie());
            } else {
                log.info("No change for bestuurlijkgebied id: {}, identificatie: {}", exBe.getId(), exBe.getIdentificatie());
            }
        }

        return result == null ? 0L : result.getId();
    }
}
