package com.wallet.mapper;

import com.wallet.DTO.*;
import com.wallet.entity.TypeWalletMovementsEntity;
import com.wallet.entity.WalletEntity;
import com.wallet.entity.WalletMovementsEntity;
import com.wallet.enums.TypeWalletMovementsEnum;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static com.wallet.mother.WalletMother.*;

@ExtendWith(MockitoExtension.class)
@Profile("test")
class WalletMapperTest {

    @InjectMocks
    private WalletMapper useCase;

    @Test
    void toWalletEntityBySuccess() {
        WalletRequestDTO request = buildWalletRequestDTO();
        String accountAvailable = RandomStringUtils.randomNumeric(5);

        WalletEntity mapped = useCase.toWalletEntityBy(request, accountAvailable);

        assertThat(mapped)
                .extracting("account", "agency", "balance", "cpf", "createdBy")
                .containsExactly(String.valueOf(Integer.valueOf(accountAvailable) + 1),
                        "1", BigDecimal.ZERO, request.getCpf(), request.getCpf());

        Assertions.assertNotNull(mapped.getCreatedAt());

    }

    @Test
    void toWalletResponseDTOBySuccess() {
        WalletEntity entity = buildWalletEntity();

        WalletResponseDTO mapped = useCase.toWalletResponseDTOBy(entity);

        assertThat(mapped)
                .extracting("account", "agency")
                .containsExactly(entity.getAccount(), entity.getAgency());

    }

    @Test
    void toWalletBalanceResponseDTOBySuccess() {
        BigDecimal balance = buildMonetary();

        WalletBalanceResponseDTO mapped = useCase.toWalletBalanceResponseDTOBy(balance);

        Assertions.assertEquals(toAmericanMoney(balance), mapped.getBalance());
    }

    @Test
    void toWalletMovementsEntityBySuccess() {
        DepositRequestDTO request = buildDepositRequestDTO();
        WalletEntity target = buildWalletEntity();
        TypeWalletMovementsEntity typeWalletMovements = buildTypeWalletMovementsEntity(TypeWalletMovementsEnum.DEPOSIT);

        WalletMovementsEntity mapped = useCase.toWalletMovementsEntityBy(request, target, typeWalletMovements);

        assertThat(mapped)
                .extracting("targetWallet", "typeWalletMovements", "amount", "cpf", "createdBy")
                .containsExactly(target, typeWalletMovements, request.getAmount(), request.getCpf(), request.getCpf());

    }

    @Test
    void toWalletMovementsEntityBy2Success() {
        WithdrawalRequestDTO request = buildWithdrawalRequestDTO();
        WalletEntity target = buildWalletEntity();
        TypeWalletMovementsEntity typeWalletMovements = buildTypeWalletMovementsEntity(TypeWalletMovementsEnum.DEPOSIT);

        WalletMovementsEntity mapped = useCase.toWalletMovementsEntityBy(request, target, typeWalletMovements);

        assertThat(mapped)
                .extracting("targetWallet", "typeWalletMovements", "amount", "cpf", "createdBy")
                .containsExactly(target, typeWalletMovements, request.getAmount().multiply(BigDecimal.valueOf(-1)), request.getCpf(), request.getCpf());

    }

    @Test
    void totoWalletMovementsEntityBy3Success() {
        BigDecimal amount = buildMonetary();
        TypeWalletMovementsEntity typeWalletMovements = buildTypeWalletMovementsEntity(TypeWalletMovementsEnum.DEPOSIT);

        WalletMovementsEntity mapped = useCase.toWalletMovementsEntityBy(amount, CPF, typeWalletMovements);

        assertThat(mapped)
                .extracting("typeWalletMovements", "amount", "cpf", "createdBy")
                .containsExactly(typeWalletMovements, amount, CPF, CPF);
    }

    private <T extends Number> String toAmericanMoney(T value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,##0.00", symbols);
        return decimalFormat.format(value);
    }
}