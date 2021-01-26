package bsoft.com.ambtsgrenzen;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class AmbtsgrenzenApplication {

    public static void main(String[] args) {

        SpringApplication.run(AmbtsgrenzenApplication.class, args);
    }

}
