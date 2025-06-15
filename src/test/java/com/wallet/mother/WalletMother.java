package com.wallet.mother;

import com.wallet.DTO.*;
import com.wallet.entity.SystemParameterEntity;
import com.wallet.entity.TypeWalletMovementsEntity;
import com.wallet.entity.WalletEntity;
import com.wallet.enums.TypeWalletMovementsEnum;
import com.wallet.utils.Constants;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.text.RandomStringGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WalletMother {

    public static final String CPF = "53805087071";

    public static DepositRequestDTO buildDepositRequestDTO() {
        return DepositRequestDTO.builder()
                .cpf(CPF)
                .amount(buildMonetary())
                .build();
    }

    public static WithdrawalRequestDTO buildWithdrawalRequestDTO() {
        return WithdrawalRequestDTO.builder()
                .cpf(CPF)
                .amount(buildMonetary())
                .build();
    }

    public static WalletEntity buildWalletEntity() {
        return WalletEntity.builder()
                .account(RandomStringUtils.randomNumeric(3))
                .agency(RandomStringUtils.randomNumeric(1))
                .balance(buildMonetary())
                .cpf(CPF)
                .version(buildNumeric())
                .createdBy(CPF)
                .createdAt(LocalDateTime.now())
                .updatedBy(CPF)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static TypeWalletMovementsEntity buildTypeWalletMovementsEntity(TypeWalletMovementsEnum typeWalletMovements) {
        return TypeWalletMovementsEntity.builder()
                .description(buildText(20))
                .name(typeWalletMovements.name())
                .createdBy(CPF)
                .createdAt(LocalDateTime.now())
                .updatedBy(CPF)
                .updatedAt(LocalDateTime.now())
                .build();

    }

    public static WalletRequestDTO buildWalletRequestDTO() {
        return WalletRequestDTO.builder()
                .cpf(CPF)
                .build();
    }

    public static SystemParameterEntity buildSystemParameterEntity() {
        return SystemParameterEntity.builder()
                .data(RandomStringUtils.randomNumeric(5))
                .name(Constants.LAST_NUMBER_ACCOUNT)
                .version(buildNumeric())
                .createdBy(CPF)
                .createdAt(LocalDateTime.now())
                .updatedBy(CPF)
                .updatedAt(LocalDateTime.now())
                .build();

    }

    public static WalletResponseDTO buildWalletResponseDTO() {
        return WalletResponseDTO.builder()
                .account(RandomStringUtils.randomNumeric(5))
                .agency(RandomStringUtils.randomNumeric(1))
                .build();
    }

    public static WalletBalanceResponseDTO buildWalletBalanceResponseDTO() {
        return WalletBalanceResponseDTO.builder()
                .balance(String.valueOf(buildMonetary()))
                .build();

    }

    public static TransferMoneyWalletRequestDTO buildTransferMoneyWalletRequestDTO() {
        return TransferMoneyWalletRequestDTO.builder()
                .amount(buildMonetary())
                .sourceCpf(CPF)
                .targetCpf("71146736045")
                .build();
    }

    public static BigDecimal buildMonetary() {
        return new BigDecimal(String.join(".", RandomStringUtils.randomNumeric(5), RandomStringUtils.randomNumeric(2)));
    }

    public static Integer buildNumeric() {
        return Integer.valueOf(RandomStringUtils.randomNumeric(2));
    }

    public static String buildText(int size) {
        return new RandomStringGenerator.Builder()
                        .build()
                        .generate(size);

    }
}
