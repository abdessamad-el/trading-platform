package io.trading.platform.persistence.repository;

import io.trading.platform.persistence.entity.Fill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FillRepository extends JpaRepository<Fill, UUID> {
	Fill findByProviderTradeId(String providerTradeId);
}
