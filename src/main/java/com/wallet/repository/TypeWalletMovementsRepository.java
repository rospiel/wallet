package com.wallet.repository;

import com.wallet.entity.TypeWalletMovementsEntity;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeWalletMovementsRepository extends JpaRepository<TypeWalletMovementsEntity, Long> {

    @QueryHints({ @QueryHint(name = "org.hibernate.readOnly", value = "true")})
    Optional<TypeWalletMovementsEntity> findByName(String name);
}
