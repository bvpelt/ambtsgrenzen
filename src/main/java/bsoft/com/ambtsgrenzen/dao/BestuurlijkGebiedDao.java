package bsoft.com.ambtsgrenzen.dao;

import bsoft.com.ambtsgrenzen.database.BestuurlijkGebied;
import bsoft.com.ambtsgrenzen.repository.BestuurlijkGebiedRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Slf4j
public class BestuurlijkGebiedDao {
    private final DataSource dataSourcePg;
    private final PlatformTransactionManager transactionManagerPg;

    @Autowired
    private BestuurlijkGebiedRepository bestuurlijkGebiedRepository;

    @Autowired
    public BestuurlijkGebiedDao(@Qualifier("dataSource") final DataSource dataSource,
                       final PlatformTransactionManager transactionManagerPg,
                        final BestuurlijkGebiedRepository bestuurlijkGebiedRepository) {

        log.debug("Create BestuurlijkGebiedDao - datasource: {}, transactionmanager: {}", dataSource.toString(), transactionManagerPg.toString());
        this.dataSourcePg = dataSource;
        this.transactionManagerPg = transactionManagerPg;
        this.bestuurlijkGebiedRepository = bestuurlijkGebiedRepository;
    }

    @Bean
    public BestuurlijkGebiedDao getDao() {
        return this;
    }

    public long save(BestuurlijkGebied bestuurlijkGebied) {
        log.info("Before save, repository: {} bestuurlijkgebied - identificatie: {}", bestuurlijkGebiedRepository, bestuurlijkGebied.getIdentificatie());

        BestuurlijkGebied result = bestuurlijkGebiedRepository.save(bestuurlijkGebied);

        log.info("After save, repository: {} bestuurlijkgebied - identificatie: {} - id: {}", bestuurlijkGebiedRepository, result.getIdentificatie(), result.getId());

        return (result != null? result.getId() : 0);
    }
}
