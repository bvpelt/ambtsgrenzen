package bsoft.com.ambtsgrenzen.repository;

import bsoft.com.ambtsgrenzen.database.BestuurlijkGebied;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BestuurlijkGebiedRepository extends JpaRepository<BestuurlijkGebied, Long> {
}
