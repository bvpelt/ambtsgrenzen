package bsoft.com.ambtsgrenzen;

import bsoft.com.ambtsgrenzen.client.AmbtsgrenzenClient;
import bsoft.com.ambtsgrenzen.dao.BestuurlijkGebiedDao;
import bsoft.com.ambtsgrenzen.model.*;
import bsoft.com.ambtsgrenzen.model.Geometry;
import bsoft.com.ambtsgrenzen.repository.BestuurlijkGebiedRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;

@Slf4j
@SpringBootTest
//@DataJpaTest
//@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
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

		int i = 0;
		int j = 0;
		for (i = 0; i < bestuurlijkGebied.length; i++) {
			j++;
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
			// log.info("                coordinates: {}", geometry.getCoordinates());
			log.info("            - domein: {}", bestuurlijkGebied[i].getDomein());
			log.info("            - type: {}", bestuurlijkGebied[i].getType());

			bsoft.com.ambtsgrenzen.database.BestuurlijkGebied bg = new bsoft.com.ambtsgrenzen.database.BestuurlijkGebied();
			bg.setIdentificatie(bestuurlijkGebied[i].getIdentificatie());
			bg.setDomein(bestuurlijkGebied[i].getDomein());
			bg.setType(bestuurlijkGebied[i].getType());

			double[][][] coords = geometry.getCoordinates();
			double[][] lines;
			Coordinate[] geoCoords = new Coordinate[coords[0].length];
			for (int k = 0; k < coords.length; k++) {
				log.info("line[{}] size: {}", k, coords[k].length);
				lines = coords[k];
				for (int l = 0; l < lines.length; l++) {
					//       log.info("lines[{}] size: {}", l, lines[l].length);
					Coordinate c = new Coordinate(lines[l][0], lines[l][1]);
					geoCoords[l] = c;
				}
			}

			GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 28992);
			CoordinateArraySequence cas = new CoordinateArraySequence(geoCoords);
			LinearRing linearRing = new LinearRing(cas, geometryFactory);
			log.info("linearRing closed: {}", linearRing.isClosed());

			Polygon polygon = new Polygon(linearRing, null, geometryFactory);
			bg.setGeometry(polygon);

		}

		HalLinks halLinks = client.getBestuurlijkeGrens().getLinks();
		log.info("next: {}", halLinks.getNext() != null ? halLinks.getNext().getHref() : "");
		String next = halLinks.getNext().getHref();
		log.info("self: {}", halLinks.getSelf() != null ? halLinks.getSelf().getHref() : "");
		log.info("prev: {}", halLinks.getPrev() != null ? halLinks.getPrev().getHref()  : "");


		if (next.length()>0) {
			bestuurlijkGebied = client.getBestuurlijkeGrens(next).getEmbedded().getBestuurlijkeGebieden();
			for (i = 0; i < bestuurlijkGebied.length; i++) {

				log.info("Element[{}] - identificatie: {}", j, bestuurlijkGebied[i].getIdentificatie());
				OpenbaarLichaam openbaarLichaam = bestuurlijkGebied[i].getEmbedded().getOpenbaarLichaam();
				log.info("            -- openbaarlichaam - code: {} type: {} naam: {} link self: {}", openbaarLichaam.getCode(), openbaarLichaam.getType(), openbaarLichaam.getNaam(), openbaarLichaam.getLinks().getSelf().getHref());
				MetaData metaData = bestuurlijkGebied[i].getEmbedded().getMetadata();
				log.info("            -- metadata - beginGeldigheid: {}", metaData.getBeginGeldigheid());
				SelfLink link = bestuurlijkGebied[i].getLinks();
				log.info("            - links self: {}", link.getSelf());
				log.info("            - geometrie");
				Geometry geometry = bestuurlijkGebied[i].getGeometrie();
				log.info("                type: {}", geometry.getType());
				// log.info("                coordinates: {}", geometry.getCoordinates());
				log.info("            - domein: {}", bestuurlijkGebied[i].getDomein());
				log.info("            - type: {}", bestuurlijkGebied[i].getType());
				j++;
			}
		}
		log.info("Test02 - Ready");
	}
}
