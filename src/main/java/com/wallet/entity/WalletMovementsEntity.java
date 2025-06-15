package com.wallet.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wallet_movements")
public class WalletMovementsEntity extends BaseEntity {


    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "target_wallet_id")
    private WalletEntity targetWallet;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "source_wallet_id")
    private WalletEntity sourceWallet;

    @ManyToOne
    @JoinColumn(name = "type_wallet_movements_id")
    private TypeWalletMovementsEntity typeWalletMovements;

    @Column
    private BigDecimal amount;

    @Column(name = "amount_after_operation")
    private BigDecimal amountAfterOperation;

    @Column
    private String cpf;

    @Column(name = "created_by")
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
