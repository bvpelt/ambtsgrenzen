package bsoft.com.ambtsgrenzen.database;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity(name = "OpenbaarLichaam")
@Table(name = "OPENBAARLICHAAM")
public class OpenbaarLichaam {
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    @Column(name = "ID")
    private Long Id;

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
}
