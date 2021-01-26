package bsoft.com.ambtsgrenzen.database;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

import javax.persistence.*;


@Data
@Entity(name = "BestuurlijkGebied")
@Table(name = "BESTUURLIJKGEBIED")
public class BestuurlijkGebied {
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    @Column(name = "ID")
    private Long id;

    @Column(name = "IDENTIFICATIE")
    private String identificatie;

    @Column(name = "DOMEIN")
    private String domein;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "GEOMETRY")
    private Geometry geometry;
}
