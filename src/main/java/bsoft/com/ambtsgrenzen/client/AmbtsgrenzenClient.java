package bsoft.com.ambtsgrenzen.client;

import bsoft.com.ambtsgrenzen.model.AmbtsgrenzenResponse;
import bsoft.com.ambtsgrenzen.model.BestuurlijkGebied;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;


@Slf4j
@NoArgsConstructor
public class AmbtsgrenzenClient {
    private final String bestuurlijkeGrensUri = "https://brk.basisregistraties.overheid.nl/api/bestuurlijke-grenzen/v2/bestuurlijke-gebieden";
    private final String X_API_KEY = "9c760bd1-d89e-481b-a91c-851ce59a3f45";


    public AmbtsgrenzenResponse getBestuurlijkeGrens() {
        log.info("AmbtsgrenzenClient - getBestuurlijkeGrens without parameters");
        BestuurlijkGebied[] bestuurlijkGebied;
        AmbtsgrenzenResponse ambtsgrenzenResponse;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("ContentType", MediaType.APPLICATION_JSON_VALUE);
        headers.add("x-api-key", X_API_KEY);
        HttpEntity<AmbtsgrenzenResponse> entity = new HttpEntity<AmbtsgrenzenResponse>(new AmbtsgrenzenResponse(), headers);

        ResponseEntity<AmbtsgrenzenResponse> ambtsgrenzenResponseResponseEntity = restTemplate.exchange(bestuurlijkeGrensUri, HttpMethod.GET, entity, AmbtsgrenzenResponse.class);

        HttpStatus httpStatus = ambtsgrenzenResponseResponseEntity.getStatusCode();
        if (httpStatus.is2xxSuccessful()) {
            ambtsgrenzenResponse = ambtsgrenzenResponseResponseEntity.getBody();
        } else {
            log.error("Http status: {} - {}", httpStatus.toString(), httpStatus.name());
            ambtsgrenzenResponse = null;
        }

        log.info("AmbtsgrenzenClient - getBestuurlijkeGrens response next: {}", ambtsgrenzenResponse.getLinks().getNext().getHref());
        return ambtsgrenzenResponse;
    }

    public AmbtsgrenzenResponse getBestuurlijkeGrens(final String url) {
        log.info("AmbtsgrenzenClient - getBestuurlijkeGrens with parameter url: {}", url);
        BestuurlijkGebied[] bestuurlijkGebied;


        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("ContentType", MediaType.APPLICATION_JSON_VALUE);
        headers.add("x-api-key", X_API_KEY);
        HttpEntity<AmbtsgrenzenResponse> entity = new HttpEntity<AmbtsgrenzenResponse>(new AmbtsgrenzenResponse(), headers);
        AmbtsgrenzenResponse ambtsgrenzenResponse = null;

        ResponseEntity<AmbtsgrenzenResponse> ambtsgrenzenResponseResponseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, AmbtsgrenzenResponse.class);
        HttpStatus httpStatus = ambtsgrenzenResponseResponseEntity.getStatusCode();
        if (httpStatus.is2xxSuccessful()) {
            ambtsgrenzenResponse = ambtsgrenzenResponseResponseEntity.getBody();
            log.info("AmbtsgrenzenClient - getBestuurlijkeGrens response next: {}", ambtsgrenzenResponse.getLinks().getNext() == null ? "null" : ambtsgrenzenResponse.getLinks().getNext().getHref());
        } else {
            log.error("Http status: {} - {}", httpStatus.toString(), httpStatus.name());
        }

        return ambtsgrenzenResponse;
    }

}
