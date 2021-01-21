package bsoft.com.ambtsgrenzen.repository;

import bsoft.com.ambtsgrenzen.database.BestuurlijkGebied;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface BestuurlijkGebiedRepository extends CrudRepository<BestuurlijkGebied, Long> {
}