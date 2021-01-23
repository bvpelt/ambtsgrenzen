package bsoft.com.ambtsgrenzen.client;

import bsoft.com.ambtsgrenzen.model.AmbtsgrenzenResponse;
import bsoft.com.ambtsgrenzen.model.BestuurlijkGebied;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;


@Slf4j
public class AmbtsgrenzenClient {

    private final String uri = "http://jsonplaceholder.typicode.com/posts/{id}";
    private final String bestuurlijkeGrensUri = "https://brk.basisregistraties.overheid.nl/api/bestuurlijke-grenzen/v2/bestuurlijke-gebieden";
    private final String X_API_KEY = "9c760bd1-d89e-481b-a91c-851ce59a3f45";

    public AmbtsgrenzenClient() {

    }


    public AmbtsgrenzenResponse getBestuurlijkeGrens() {
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
        return ambtsgrenzenResponse;
    }

    public AmbtsgrenzenResponse getBestuurlijkeGrens(final String url) {
        BestuurlijkGebied[] bestuurlijkGebied;


        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("ContentType", MediaType.APPLICATION_JSON_VALUE);
        headers.add("x-api-key", X_API_KEY);
        HttpEntity<AmbtsgrenzenResponse> entity = new HttpEntity<AmbtsgrenzenResponse>(new AmbtsgrenzenResponse(), headers);

        ResponseEntity<AmbtsgrenzenResponse> ambtsgrenzenResponseResponseEntity = restTemplate.exchange(bestuurlijkeGrensUri, HttpMethod.GET, entity, AmbtsgrenzenResponse.class);
        AmbtsgrenzenResponse ambtsgrenzenResponse = ambtsgrenzenResponseResponseEntity.getBody();

        return ambtsgrenzenResponse;
    }

}
