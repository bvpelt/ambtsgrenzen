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
        bsoft.com.ambtsgrenzen.database.OpenbaarLichaam existingOpenbaarLichaam = openbaarLichaamRepository.findByCode(openbaarLichaam.getCode());
        if (existingOpenbaarLichaam == null) {
            log.info("LoadOpenbareLichamen - persistOpenbaarLichaam not found code: {} insert", openbaarLichaam.getCode());
            ol = openbaarLichaamRepository.save(openbaarLichaam);
            result = ol.getId();
        } else {
            log.info("LoadOpenbareLichamen - persistOpenbaarLichaam found code: {} update", openbaarLichaam.getCode());
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

                log.info("LoadOpenbareLichamen - save exisiting id: {} code: {}, type: {}, name: {}", existingOpenbaarLichaam.getId(), existingOpenbaarLichaam.getCode(), existingOpenbaarLichaam.getType(), existingOpenbaarLichaam.getName());
                ol = openbaarLichaamRepository.save(existingOpenbaarLichaam);
                result = ol.getId();
            } else {
                log.info("LoadOpenbareLichamen - persistOpenbaarLichaam found code: {} no changes", openbaarLichaam.getCode());
            }
        }

        return result;
    }
}
