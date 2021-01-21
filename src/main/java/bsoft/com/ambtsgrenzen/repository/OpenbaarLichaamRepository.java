package bsoft.com.ambtsgrenzen.repository;

import bsoft.com.ambtsgrenzen.database.OpenbaarLichaam;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpenbaarLichaamRepository extends CrudRepository<OpenbaarLichaam, Long> {
}