package bsoft.com.ambtsgrenzen.client;

import bsoft.com.ambtsgrenzen.model.AmbtsgrenzenResponse;
import bsoft.com.ambtsgrenzen.model.BestuurlijkGebied;
import bsoft.com.ambtsgrenzen.model.Post;
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

    public Post getPost() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("ContentType", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<Post> entity = new HttpEntity<Post>(new Post(), headers);

        ResponseEntity<Post> postEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, Post.class, 9);
        Post result = postEntity.getBody();

        log.info("AmbtsgrenzenClient - id: {} userid: {} title: {} body: {}", result.getId(), result.getUserId(), result.getTitle(), result.getBody());

        return result;
    }

    /*
     Structuur
     _embedded {
        bestuurlijkeGebieden {[]},
     _links {
        next:
            href: http://...
        prev:
            href: http://..
        self:
            href: http://...
     }

     }

    */
    public AmbtsgrenzenResponse getBestuurlijkeGrens() {
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
