package com.wallet.repository;

import com.wallet.entity.WalletEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<WalletEntity, Long> {

    Optional<WalletEntity> findByCpf(String cpf);

    @Query(" from WalletEntity w " +
           "where w.cpf = :cpf")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<WalletEntity> findByCpfWithPessimisticWrite(String cpf);
}
