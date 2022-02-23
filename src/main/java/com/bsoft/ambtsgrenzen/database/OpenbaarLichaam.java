package com.bsoft.ambtsgrenzen.database;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Data
@Entity(name = "OpenbaarLichaam")
@Table(name = "OPENBAARLICHAAM")
public class OpenbaarLichaam {
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "OIN")
    private String oin;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "NAME")
    private String name;

    @Column(name = "BESTUURSLAAG")
    private String bestuurslaag;

    @Column(name = "BEGINGELDIGHEID")
    private Date beginGeldigheid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpenbaarLichaam that = (OpenbaarLichaam) o;
        return Objects.equals(code, that.code) && Objects.equals(oin, that.oin) && Objects.equals(type, that.type) && Objects.equals(name, that.name) && Objects.equals(bestuurslaag, that.bestuurslaag) && Objects.equals(beginGeldigheid, that.beginGeldigheid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, oin, type, name, bestuurslaag, beginGeldigheid);
    }
}
