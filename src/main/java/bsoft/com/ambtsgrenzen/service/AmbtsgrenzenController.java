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

        log.info("AmbtsgrenzenController - start loader");
        int status = loadBestuurlijkeGrenzen.load();
        log.info("AmbtsgrenzenController - end   loader");

        ambtsgrenzenResponse.setStatus(status);
        return ambtsgrenzenResponseResponseEntity;
    }
}
