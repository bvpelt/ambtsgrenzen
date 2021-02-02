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
import java.util.Optional;

@NoArgsConstructor
@Slf4j
@Service
public class LoadOpenbareLichamen {

    private final String scheme = "https";
    private final String host = "brk.basisregistraties.overheid.nl";
    private final String path = "/api/bestuurlijke-grenzen/v2";
    private final String openbaarLichamenUri = scheme + "://" + host + path + "/openbare-lichamen?&pageSize=10";

    private OpenbaarLichaamRepository openbaarLichaamRepository = null;
    private long status = 0L;
    private String next;
    private AmbtsgrenzenClient client;

    @Autowired
    public LoadOpenbareLichamen(final OpenbaarLichaamRepository openbaarLichaamRepository) {
        this.openbaarLichaamRepository = openbaarLichaamRepository;
        this.client = new AmbtsgrenzenClient();
    }

    public long load() {
        log.info("LoadOpenbareLichamen - start load");
        int page = 1;
        String url = openbaarLichamenUri + "&page=" + page;
        status = getNextPage(url);
        log.info("LoadOpenbareLichamen - load status: {} next: {}", status, next);
        while (next != null && next.length() > 0) {
            page++;
            url = openbaarLichamenUri + "&page=" + page;
            status = getNextPage(url);
            log.info("LoadOpenbareLichamen - load status: {} next: {}", status, next);
        }
        log.info("LoadOpenbareLichamen - end   load");
        return status;
    }



    private long getNextPage(String url) {
        log.info("LoadOpenbareLichamen - start getNextPage");

        OpenbareLichamenResponse response = client.getOpenbareLichamen(url);
        if (response != null) {
           OpenbaarLichaam[] openbareLichamen = response.getEmbedded().getOpenbareLichamen();
            log.info("Aantal openbare lichamen: {}", openbareLichamen.length);

            int i = 0;
            for (i = 0; i < openbareLichamen.length; i++) {
                log.info("Element[{}] - code: {}", i, openbareLichamen[i].getCode());
                OpenbaarLichaam openbaarLichaam = openbareLichamen[i];
                log.info(" -- openbaarlichaam - code: {} type: {} naam: {} link self: {}", openbaarLichaam.getCode(), openbaarLichaam.getType(), openbaarLichaam.getName(), openbaarLichaam.getLinks().getSelf().getHref());

                bsoft.com.ambtsgrenzen.database.OpenbaarLichaam ol = new bsoft.com.ambtsgrenzen.database.OpenbaarLichaam();
                ol.setCode(openbaarLichaam.getCode());
                ol.setOin(openbaarLichaam.getOin());
                ol.setType(openbaarLichaam.getType());
                ol.setName(openbaarLichaam.getName());
                ol.setBestuurslaag(openbaarLichaam.getBestuurslaag());

                if (persistOpenbaarLichaam(ol) > 0L) {
                    status++;
                }
            }
            next = response.getLinks().getNext() == null ? "" : response.getLinks().getNext().getHref();

            log.info("LoadOpenbareLichamen - found next: {}", next);
        } else {
            next = "";
        }

        log.info("LoadOpenbareLichamen - end   getNextPage");
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
    public long persistOpenbaarLichaam(bsoft.com.ambtsgrenzen.database.OpenbaarLichaam openbaarLichaam) {
        bsoft.com.ambtsgrenzen.database.OpenbaarLichaam ol = null;
        long result = 0L;
        Optional<bsoft.com.ambtsgrenzen.database.OpenbaarLichaam> existingOpenbaarLichaam = openbaarLichaamRepository.findByCode(openbaarLichaam.getCode());

        if (existingOpenbaarLichaam.isEmpty()) {
            ol = openbaarLichaamRepository.save(openbaarLichaam);
            result = ol.getId();
            log.info("LoadOpenbareLichamen - new openbaarlichaam id: {}, code: {}", ol.getId(), ol.getCode());
        } else {
            bsoft.com.ambtsgrenzen.database.OpenbaarLichaam exOl = existingOpenbaarLichaam.get();
            log.info("LoadOpenbareLichamen - persistOpenbaarLichaam found code: {} update", openbaarLichaam.getCode());
            if (!openbaarLichaam.equals(exOl)) {
                if ((exOl.getOin() != null ) && !exOl.getOin().equals(openbaarLichaam.getOin())) {
                    exOl.setOin(openbaarLichaam.getOin());
                }

                if ((exOl.getType() != null) && !exOl.getType().equals(openbaarLichaam.getType())) {
                    exOl.setType(openbaarLichaam.getType());
                }

                if ((exOl.getName() != null) && !exOl.getName().equals(openbaarLichaam.getName())) {
                    exOl.setName(openbaarLichaam.getName());
                }

                if ((exOl.getBestuurslaag() != null) && !exOl.getBestuurslaag().equals(openbaarLichaam.getBestuurslaag())) {
                    exOl.setBestuurslaag(openbaarLichaam.getBestuurslaag());
                }

                if ((exOl.getBeginGeldigheid() != null) && !exOl.getBeginGeldigheid().equals(openbaarLichaam.getBeginGeldigheid())) {
                    exOl.setBeginGeldigheid(openbaarLichaam.getBeginGeldigheid());
                }

                log.info("LoadOpenbareLichamen - changed openbaarlichaam id: {} code: {}", exOl.getId(), exOl.getCode());
                ol = openbaarLichaamRepository.save(exOl);
                result = ol.getId();
            } else {
                log.info("LoadOpenbareLichamen - no changes for openbaarlichaar id: {}, code: {}", openbaarLichaam.getId(), openbaarLichaam.getCode());
            }
        }

        return result;
    }
}
