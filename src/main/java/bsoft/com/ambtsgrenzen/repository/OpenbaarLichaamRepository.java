package bsoft.com.ambtsgrenzen.repository;

import bsoft.com.ambtsgrenzen.database.OpenbaarLichaam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OpenbaarLichaamRepository extends JpaRepository<OpenbaarLichaam, Long> {
    Optional<OpenbaarLichaam> findByCode(final String code);
}
