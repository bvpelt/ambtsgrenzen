package bsoft.com.ambtsgrenzen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class AmbtsgrenzenApplication {

    public static void main(String[] args) {
        SpringApplication.run(AmbtsgrenzenApplication.class, args);
    }

}
