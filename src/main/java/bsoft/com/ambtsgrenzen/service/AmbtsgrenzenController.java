package bsoft.com.ambtsgrenzen.service;

import bsoft.com.ambtsgrenzen.proces.LoadBestuurlijkeGrenzen;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor
@Slf4j
@RestController
public class AmbtsgrenzenController {

    private LoadBestuurlijkeGrenzen loadBestuurlijkeGrenzen = null;

    @Autowired
    public AmbtsgrenzenController(LoadBestuurlijkeGrenzen loadBestuurlijkeGrenzen) {
        this.loadBestuurlijkeGrenzen = loadBestuurlijkeGrenzen;
    }

    @RequestMapping(value = "/bestuurlijkgebied")
    public ResponseEntity<AmbtsgrenzenResponse> getBestuurlijkGebied() {
        AmbtsgrenzenResponse ambtsgrenzenResponse = new AmbtsgrenzenResponse();
        ResponseEntity<AmbtsgrenzenResponse> ambtsgrenzenResponseResponseEntity = ResponseEntity.ok(ambtsgrenzenResponse);

        long status = 0;
        String melding = "";
        try {
            log.info("AmbtsgrenzenController - start loader");
            status = loadBestuurlijkeGrenzen.load();
            log.info("AmbtsgrenzenController - end   loader status: {}", status);
        } catch (Exception e) {
            melding = e.getMessage();
        }
        ambtsgrenzenResponse.setStatus(status > 0 ? 1 : 0);
        ambtsgrenzenResponse.setAantalBestuurlijkeGebieden(status);
        ambtsgrenzenResponse.setMelding(melding);

        return ambtsgrenzenResponseResponseEntity;
    }
}
