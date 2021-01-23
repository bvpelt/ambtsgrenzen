package bsoft.com.ambtsgrenzen.repository;

import bsoft.com.ambtsgrenzen.database.OpenbaarLichaam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpenbaarLichaamRepository extends JpaRepository<OpenbaarLichaam, Long> {
}
