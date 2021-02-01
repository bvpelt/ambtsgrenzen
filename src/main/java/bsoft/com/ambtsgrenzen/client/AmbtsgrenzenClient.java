package bsoft.com.ambtsgrenzen.client;

import bsoft.com.ambtsgrenzen.model.BestuurlijkeGrenzenResponse;
import bsoft.com.ambtsgrenzen.model.BestuurlijkGebied;
import bsoft.com.ambtsgrenzen.model.OpenbaarLichaam;
import bsoft.com.ambtsgrenzen.model.OpenbareLichamenResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;


@Slf4j
@NoArgsConstructor
public class AmbtsgrenzenClient {
    private final String scheme = "https";
    private final String host = "brk.basisregistraties.overheid.nl";
    private final String path = "/api/bestuurlijke-grenzen/v2";
//    private final String bestuurlijkeGrensUri = "https://brk.basisregistraties.overheid.nl/api/bestuurlijke-grenzen/v2/bestuurlijke-gebieden";
    private final String bestuurlijkeGrensUri = scheme + "://" + host + path + "/bestuurlijke-gebieden";
    private final String openbaarLichamenUri = scheme + "://" + host + path + "/openbare-lichamen";
    private final String X_API_KEY = "9c760bd1-d89e-481b-a91c-851ce59a3f45";
    private final String ACCEPT_CRS = "epsg:28992";

    public BestuurlijkeGrenzenResponse getBestuurlijkeGrens() {
        log.info("AmbtsgrenzenClient - getBestuurlijkeGrens without parameters");
        BestuurlijkGebied[] bestuurlijkGebied;
        BestuurlijkeGrenzenResponse bestuurlijkeGrenzenResponse;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("ContentType", MediaType.APPLICATION_JSON_VALUE);
        headers.add("x-api-key", X_API_KEY);
        headers.add("accept-crs", ACCEPT_CRS);
        HttpEntity<BestuurlijkeGrenzenResponse> entity = new HttpEntity<BestuurlijkeGrenzenResponse>(new BestuurlijkeGrenzenResponse(), headers);

        ResponseEntity<BestuurlijkeGrenzenResponse> ambtsgrenzenResponseResponseEntity = restTemplate.exchange(bestuurlijkeGrensUri, HttpMethod.GET, entity, BestuurlijkeGrenzenResponse.class);

        HttpStatus httpStatus = ambtsgrenzenResponseResponseEntity.getStatusCode();
        if (httpStatus.is2xxSuccessful()) {
            bestuurlijkeGrenzenResponse = ambtsgrenzenResponseResponseEntity.getBody();
        } else {
            log.error("Http status: {} - {}", httpStatus.toString(), httpStatus.name());
            bestuurlijkeGrenzenResponse = null;
        }

        log.info("AmbtsgrenzenClient - getBestuurlijkeGrens response next: {}", bestuurlijkeGrenzenResponse.getLinks().getNext().getHref());
        return bestuurlijkeGrenzenResponse;
    }

    public BestuurlijkeGrenzenResponse getBestuurlijkeGrens(final String url) {
        log.info("AmbtsgrenzenClient - getBestuurlijkeGrens with parameter url: {}", url);
        BestuurlijkGebied[] bestuurlijkGebied;


        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("ContentType", MediaType.APPLICATION_JSON_VALUE);
        headers.add("x-api-key", X_API_KEY);
        headers.add("accept-crs", ACCEPT_CRS);
        HttpEntity<BestuurlijkeGrenzenResponse> entity = new HttpEntity<BestuurlijkeGrenzenResponse>(new BestuurlijkeGrenzenResponse(), headers);
        BestuurlijkeGrenzenResponse bestuurlijkeGrenzenResponse = null;

        ResponseEntity<BestuurlijkeGrenzenResponse> ambtsgrenzenResponseResponseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, BestuurlijkeGrenzenResponse.class);
        HttpStatus httpStatus = ambtsgrenzenResponseResponseEntity.getStatusCode();
        if (httpStatus.is2xxSuccessful()) {
            bestuurlijkeGrenzenResponse = ambtsgrenzenResponseResponseEntity.getBody();
            log.info("AmbtsgrenzenClient - getBestuurlijkeGrens response next: {}", bestuurlijkeGrenzenResponse.getLinks().getNext() == null ? "null" : bestuurlijkeGrenzenResponse.getLinks().getNext().getHref());
        } else {
            log.error("Http status: {} - {}", httpStatus.toString(), httpStatus.name());
        }

        return bestuurlijkeGrenzenResponse;
    }

    public OpenbareLichamenResponse getOpenbareLichamen(final String url) {
        log.info("AmbtsgrenzenClient - getOpenbareLichamen without parameters");
        OpenbaarLichaam[] openbaarLichaam;
        OpenbareLichamenResponse openbareLichamenResponse;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("ContentType", MediaType.APPLICATION_JSON_VALUE);
        headers.add("x-api-key", X_API_KEY);
        headers.add("accept-crs", ACCEPT_CRS);
        HttpEntity<OpenbareLichamenResponse> entity = new HttpEntity<OpenbareLichamenResponse>(new OpenbareLichamenResponse(), headers);

        ResponseEntity<OpenbareLichamenResponse> openbareLichamenResponseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, OpenbareLichamenResponse.class);

        HttpStatus httpStatus = openbareLichamenResponseEntity.getStatusCode();
        if (httpStatus.is2xxSuccessful()) {
            openbareLichamenResponse = openbareLichamenResponseEntity.getBody();
        } else {
            log.error("Http status: {} - {}", httpStatus.toString(), httpStatus.name());
            openbareLichamenResponse = null;
        }

        return openbareLichamenResponse;
    }
}
