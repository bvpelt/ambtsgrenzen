package com.bsoft.ambtsgrenzen.database;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.locationtech.jts.geom.Geometry;

import javax.persistence.*;
import java.util.Objects;

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

    @OneToOne(optional = false)
    @JoinColumn(name = "OPENBAARLICHAAM_ID", referencedColumnName = "ID")
    private OpenbaarLichaam openbaarLichaam;

    @Column(name = "GEOMETRY")
    private Geometry geometry;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BestuurlijkGebied that = (BestuurlijkGebied) o;
        return Objects.equals(identificatie, that.identificatie) && Objects.equals(domein, that.domein) && Objects.equals(type, that.type) && Objects.equals(openbaarLichaam, that.openbaarLichaam) && Objects.equals(geometry, that.geometry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificatie, domein, type, openbaarLichaam, geometry);
    }
}
