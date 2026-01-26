package io.trading.platform.persistence.repository;


import io.trading.platform.persistence.entity.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstrumentRepository extends JpaRepository<Instrument, String> {
}
