package bsoft.com.ambtsgrenzen.proces;

import bsoft.com.ambtsgrenzen.client.AmbtsgrenzenClient;
import bsoft.com.ambtsgrenzen.model.*;
import bsoft.com.ambtsgrenzen.repository.BestuurlijkGebiedRepository;
import bsoft.com.ambtsgrenzen.repository.OpenbaarLichaamRepository;
import bsoft.com.ambtsgrenzen.utils.GeometryToJTS;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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

                bsoft.com.ambtsgrenzen.database.BestuurlijkGebied bg = new bsoft.com.ambtsgrenzen.database.BestuurlijkGebied();
                bg.setIdentificatie(bestuurlijkGebied[i].getIdentificatie());
                bg.setDomein(bestuurlijkGebied[i].getDomein());
                bg.setType(bestuurlijkGebied[i].getType());

                bsoft.com.ambtsgrenzen.database.OpenbaarLichaam ol = new bsoft.com.ambtsgrenzen.database.OpenbaarLichaam();
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
    public long persistBestuurlijkGebied(bsoft.com.ambtsgrenzen.database.BestuurlijkGebied bestuurlijkGebied, bsoft.com.ambtsgrenzen.database.OpenbaarLichaam openbaarLichaam) {
        bsoft.com.ambtsgrenzen.database.OpenbaarLichaam ol = null;
        bsoft.com.ambtsgrenzen.database.BestuurlijkGebied bg = null;
        bsoft.com.ambtsgrenzen.database.BestuurlijkGebied result = null;

        bsoft.com.ambtsgrenzen.database.OpenbaarLichaam existingOpenbaarLichaam = openbaarLichaamRepository.findByCode(openbaarLichaam.getCode());
        if (existingOpenbaarLichaam == null) {
            ol = openbaarLichaamRepository.save(openbaarLichaam);
        } else {
            if (!openbaarLichaam.equals(existingOpenbaarLichaam)) {
                if (!existingOpenbaarLichaam.getOin().equals(openbaarLichaam.getOin())) {
                    existingOpenbaarLichaam.setOin(openbaarLichaam.getOin());
                }

                if (!existingOpenbaarLichaam.getType().equals(openbaarLichaam.getType())) {
                    existingOpenbaarLichaam.setType(openbaarLichaam.getType());
                }

                if (!existingOpenbaarLichaam.getName().equals(openbaarLichaam.getName())) {
                    existingOpenbaarLichaam.setName(openbaarLichaam.getName());
                }

                if (!existingOpenbaarLichaam.getBestuurslaag().equals(openbaarLichaam.getBestuurslaag())) {
                    existingOpenbaarLichaam.setBestuurslaag(openbaarLichaam.getBestuurslaag());
                }

                if (!existingOpenbaarLichaam.getBeginGeldigheid().equals(openbaarLichaam.getBeginGeldigheid())) {
                    existingOpenbaarLichaam.setBeginGeldigheid(openbaarLichaam.getBeginGeldigheid());
                }
            }
            ol = openbaarLichaamRepository.save(existingOpenbaarLichaam);
        }

        bsoft.com.ambtsgrenzen.database.BestuurlijkGebied existingBestuurlijkGebied = bestuurlijkGebiedRepository.findByIdentificatie(bestuurlijkGebied.getIdentificatie());
        if (existingBestuurlijkGebied == null) {
            bestuurlijkGebied.setOpenbaarLichaam(ol);
            result = bestuurlijkGebiedRepository.save(bestuurlijkGebied);
        } else {
            if (!bestuurlijkGebied.equals(existingBestuurlijkGebied)) {
                if (!existingBestuurlijkGebied.getGeometry().equals(bestuurlijkGebied.getGeometry())) {
                    existingBestuurlijkGebied.setGeometry(bestuurlijkGebied.getGeometry());
                }
                if (!existingBestuurlijkGebied.getDomein().equals(bestuurlijkGebied.getDomein())) {
                    existingBestuurlijkGebied.setDomein(bestuurlijkGebied.getDomein());
                }

                if (!existingBestuurlijkGebied.getType().equals(bestuurlijkGebied.getType())) {
                    existingBestuurlijkGebied.setType(bestuurlijkGebied.getType());
                }
            }

            existingBestuurlijkGebied.setOpenbaarLichaam(ol);
            result = bestuurlijkGebiedRepository.save(existingBestuurlijkGebied);
        }

        return result.getId();
    }
}
