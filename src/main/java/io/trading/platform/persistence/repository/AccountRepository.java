package io.trading.platform.persistence.repository;

import io.trading.platform.persistence.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

  Account findByProviderAccountId(String providerAccountId);
}
