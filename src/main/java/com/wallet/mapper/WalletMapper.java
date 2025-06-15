package com.wallet.mapper;

import com.wallet.DTO.*;
import com.wallet.decorators.DecimalFormatDecorator;
import com.wallet.entity.TypeWalletMovementsEntity;
import com.wallet.entity.WalletEntity;
import com.wallet.entity.WalletMovementsEntity;
import com.wallet.enums.TypeWalletMovementsEnum;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class WalletMapper extends DecimalFormatDecorator {

    public WalletEntity toWalletEntityBy(WalletRequestDTO request, String accountAvailable) {
        return WalletEntity.builder()
                .account(String.valueOf(Integer.valueOf(accountAvailable) + 1))
                .agency("1")
                .balance(BigDecimal.ZERO)
                .cpf(request.getCpf())
                .createdAt(LocalDateTime.now())
                .createdBy(request.getCpf())
                .build();
    }

    public WalletResponseDTO toWalletResponseDTOBy(WalletEntity entity) {
        return WalletResponseDTO.builder()
                .account(entity.getAccount())
                .agency(entity.getAgency())
                .build();
    }

    public WalletBalanceResponseDTO toWalletBalanceResponseDTOBy(BigDecimal balance) {
        return WalletBalanceResponseDTO.builder()
                .balance(toAmericanMoney(balance))
                .build();
    }

    public WalletMovementsEntity toWalletMovementsEntityBy(DepositRequestDTO request, WalletEntity target, TypeWalletMovementsEntity typeWalletMovements) {
        return WalletMovementsEntity.builder()
                .targetWallet(target)
                .typeWalletMovements(typeWalletMovements)
                .amount(request.getAmount())
                .cpf(request.getCpf())
                .createdBy(request.getCpf())
                .build();
    }

    public WalletMovementsEntity toWalletMovementsEntityBy(WithdrawalRequestDTO request, WalletEntity target, TypeWalletMovementsEntity typeWalletMovements) {
        return WalletMovementsEntity.builder()
                .targetWallet(target)
                .typeWalletMovements(typeWalletMovements)
                .amount(request.getAmount().multiply(BigDecimal.valueOf(-1)))
                .cpf(request.getCpf())
                .createdBy(request.getCpf())
                .build();
    }

    public WalletMovementsEntity toWalletMovementsEntityBy(BigDecimal amount, String cpf,
                                                           TypeWalletMovementsEntity typeWalletMovements) {
        return WalletMovementsEntity.builder()
                .typeWalletMovements(typeWalletMovements)
                .amount(amount)
                .cpf(cpf)
                .createdBy(cpf)
                .build();
    }
}
