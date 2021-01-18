package bsoft.com.ambtsgrenzen;

import bsoft.com.ambtsgrenzen.client.AmbtsgrenzenClient;
import bsoft.com.ambtsgrenzen.model.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class AmbtsgrenzenApplicationTests {

	@Test
	void contextLoads() {
		log.info("contextLoads...");
	}


	@Test
	void test01() {
		log.info("Test01");
		AmbtsgrenzenClient client = new AmbtsgrenzenClient();

		client.getPost();
		log.info("Test01 - Ready");
	}

	@Test
	void test02() {
		log.info("Test02");
		AmbtsgrenzenClient client = new AmbtsgrenzenClient();

		BestuurlijkGebied[] bestuurlijkGebied = client.getBestuurlijkeGrens().getEmbedded().getBestuurlijkeGebieden();
		log.info("Aantal bestuurlijke grenzen: {}", bestuurlijkGebied.length);

		for (int i = 0; i < bestuurlijkGebied.length; i++) {
			log.info("Element[{}] - identificatie: {}", i, bestuurlijkGebied[i].getIdentificatie());
			OpenbaarLichaam openbaarLichaam = bestuurlijkGebied[i].getEmbedded().getOpenbaarLichaam();
			log.info("            -- openbaarlichaam - code: {} type: {} naam: {} link self: {}", openbaarLichaam.getCode(), openbaarLichaam.getType(), openbaarLichaam.getNaam(), openbaarLichaam.getLinks().getSelf().getHref());
			MetaData metaData = bestuurlijkGebied[i].getEmbedded().getMetadata();
			log.info("            -- metadata - beginGeldigheid: {}", metaData.getBeginGeldigheid());
			SelfLink link = bestuurlijkGebied[i].getLinks();
			log.info("            - links self: {}", link.getSelf());
			log.info("            - geometrie");
			Geometry geometry = bestuurlijkGebied[i].getGeometrie();
			log.info("                type: {}", geometry.getType());
			log.info("                coordinates: {}", geometry.getCoordinates());
			log.info("            - domein: {}", bestuurlijkGebied[i].getDomein());
			log.info("            - type: {}", bestuurlijkGebied[i].getType());
		}

		HalLinks halLinks = client.getBestuurlijkeGrens().getLinks();


		log.info("Test02 - Ready");
	}
}
