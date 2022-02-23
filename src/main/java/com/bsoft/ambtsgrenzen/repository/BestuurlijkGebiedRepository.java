package com.bsoft.ambtsgrenzen.repository;

import com.bsoft.ambtsgrenzen.database.BestuurlijkGebied;
import com.bsoft.ambtsgrenzen.database.OpenbaarLichaam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BestuurlijkGebiedRepository extends JpaRepository<BestuurlijkGebied, Long> {
    Optional<BestuurlijkGebied> findByIdentificatie(final String identificatie);
    Optional<BestuurlijkGebied> findByOpenbaarLichaam(final OpenbaarLichaam openbaarLichaam);
}
