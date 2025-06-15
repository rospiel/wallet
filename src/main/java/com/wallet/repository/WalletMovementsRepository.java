package com.wallet.repository;

import com.wallet.entity.WalletMovementsEntity;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface WalletMovementsRepository extends JpaRepository<WalletMovementsEntity, Long> {

    @QueryHints({ @QueryHint(name = "org.hibernate.readOnly", value = "true")})
    @Query(value = "   select wm.amount_after_operation " +
                   "     from wallet_movements wm " +
                   "     join wallet w " +
                   "       on wm.target_wallet_id = w.id " +
                   "    where w.cpf = :cpf " +
                   "      and date_trunc('SECOND', wm.created_at) <= :date " +
                   " order by wm.created_at desc limit 1", nativeQuery = true)
    BigDecimal findBalanceBy(String cpf, LocalDateTime date);


}
