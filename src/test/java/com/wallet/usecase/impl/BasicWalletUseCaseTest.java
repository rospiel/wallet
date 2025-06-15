package com.wallet.usecase.impl;

import com.wallet.DTO.DepositRequestDTO;
import com.wallet.DTO.WithdrawalRequestDTO;
import com.wallet.entity.TypeWalletMovementsEntity;
import com.wallet.entity.WalletEntity;
import com.wallet.entity.WalletMovementsEntity;
import com.wallet.enums.TypeWalletMovementsEnum;
import com.wallet.mapper.WalletMapper;
import com.wallet.repository.WalletMovementsRepository;
import com.wallet.repository.WalletRepository;
import com.wallet.usecase.GetTypeWalletMovementsService;
import com.wallet.usecase.impl.BasicWalletUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static com.wallet.mother.WalletMother.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
@Profile("test")
class BasicWalletUseCaseTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletMovementsRepository walletMovementsRepository;

    @Mock
    private WalletMapper walletMapper;

    @Mock
    private GetTypeWalletMovementsService getTypeWalletMovementsService;

    @InjectMocks
    private BasicWalletUseCase useCase;

    @Test
    void depositSuccess() {
        DepositRequestDTO request = buildDepositRequestDTO();

        WalletEntity walletFound = buildWalletEntity();
        BigDecimal balance = walletFound.getBalance();
        BDDMockito.when(walletRepository.findByCpfWithPessimisticWrite(request.getCpf())).thenReturn(Optional.of(walletFound));

        TypeWalletMovementsEntity typeWalletMovementsEntity = buildTypeWalletMovementsEntity(TypeWalletMovementsEnum.DEPOSIT);
        BDDMockito.when(getTypeWalletMovementsService.get(TypeWalletMovementsEnum.DEPOSIT)).thenReturn(typeWalletMovementsEntity);

        WalletMovementsEntity walletMovementsEntity = new WalletMovementsEntity();
        BDDMockito.when(walletMapper.toWalletMovementsEntityBy(request, walletFound, typeWalletMovementsEntity))
            .thenReturn(walletMovementsEntity);

        useCase.deposit(request);
        BigDecimal newBalance = balance.add(request.getAmount());
        Assertions.assertEquals(walletMovementsEntity.getAmountAfterOperation(), newBalance);
        Assertions.assertEquals(walletFound.getBalance(), newBalance);


        BDDMockito.verify(walletRepository).findByCpfWithPessimisticWrite(anyString());
        BDDMockito.verify(getTypeWalletMovementsService).get(any(TypeWalletMovementsEnum.class));
        BDDMockito.verify(walletMapper).toWalletMovementsEntityBy(any(DepositRequestDTO.class), any(WalletEntity.class), any(TypeWalletMovementsEntity.class));
        BDDMockito.verify(walletMovementsRepository).save(walletMovementsEntity);
    }

    @Test
    void depositWalletEntityNotFound() {
        DepositRequestDTO request = buildDepositRequestDTO();

        BDDMockito.when(walletRepository.findByCpfWithPessimisticWrite(request.getCpf())).thenReturn(Optional.empty());

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> useCase.deposit(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"Wallet not found\"");

        BDDMockito.verify(walletRepository).findByCpfWithPessimisticWrite(anyString());
        BDDMockito.verifyNoInteractions(getTypeWalletMovementsService);
        BDDMockito.verifyNoInteractions(walletMapper);
        BDDMockito.verifyNoInteractions(walletMovementsRepository);
    }

    @Test
    void withdrawalSuccess() {
        WithdrawalRequestDTO request = buildWithdrawalRequestDTO();

        WalletEntity walletFound = buildWalletEntity();
        walletFound.setBalance(walletFound.getBalance().add(request.getAmount()));
        BigDecimal balance = walletFound.getBalance();
        BDDMockito.when(walletRepository.findByCpfWithPessimisticWrite(request.getCpf())).thenReturn(Optional.of(walletFound));

        TypeWalletMovementsEntity typeWalletMovementsEntity = buildTypeWalletMovementsEntity(TypeWalletMovementsEnum.WITHDRAWAL);
        BDDMockito.when(getTypeWalletMovementsService.get(TypeWalletMovementsEnum.WITHDRAWAL)).thenReturn(typeWalletMovementsEntity);

        WalletMovementsEntity walletMovementsEntity = new WalletMovementsEntity();
        BDDMockito.when(walletMapper.toWalletMovementsEntityBy(request, walletFound, typeWalletMovementsEntity))
                .thenReturn(walletMovementsEntity);

        useCase.withdrawal(request);
        BigDecimal newBalance = balance.subtract(request.getAmount());
        Assertions.assertEquals(walletMovementsEntity.getAmountAfterOperation(), newBalance);
        Assertions.assertEquals(walletFound.getBalance(), newBalance);


        BDDMockito.verify(walletRepository).findByCpfWithPessimisticWrite(anyString());
        BDDMockito.verify(getTypeWalletMovementsService).get(any(TypeWalletMovementsEnum.class));
        BDDMockito.verify(walletMapper).toWalletMovementsEntityBy(any(WithdrawalRequestDTO.class), any(WalletEntity.class), any(TypeWalletMovementsEntity.class));
        BDDMockito.verify(walletMovementsRepository).save(walletMovementsEntity);
    }

    @Test
    void withdrawalWalletEntityNotFound() {
        WithdrawalRequestDTO request = buildWithdrawalRequestDTO();

        BDDMockito.when(walletRepository.findByCpfWithPessimisticWrite(request.getCpf())).thenReturn(Optional.empty());

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> useCase.withdrawal(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"Wallet not found\"");

        BDDMockito.verify(walletRepository).findByCpfWithPessimisticWrite(anyString());
        BDDMockito.verifyNoInteractions(getTypeWalletMovementsService);
        BDDMockito.verifyNoInteractions(walletMapper);
        BDDMockito.verifyNoInteractions(walletMovementsRepository);
    }

    @Test
    void withdrawalInsufficientBalance() {
        WithdrawalRequestDTO request = buildWithdrawalRequestDTO();

        WalletEntity walletFound = buildWalletEntity();
        walletFound.setBalance(BigDecimal.ZERO);
        BDDMockito.when(walletRepository.findByCpfWithPessimisticWrite(request.getCpf())).thenReturn(Optional.of(walletFound));

        TypeWalletMovementsEntity typeWalletMovementsEntity = buildTypeWalletMovementsEntity(TypeWalletMovementsEnum.WITHDRAWAL);
        BDDMockito.when(getTypeWalletMovementsService.get(TypeWalletMovementsEnum.WITHDRAWAL)).thenReturn(typeWalletMovementsEntity);

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> useCase.withdrawal(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("422 UNPROCESSABLE_ENTITY \"No operation allowed\"");

        BDDMockito.verify(walletRepository).findByCpfWithPessimisticWrite(anyString());
        BDDMockito.verify(getTypeWalletMovementsService).get(any(TypeWalletMovementsEnum.class));
        BDDMockito.verifyNoInteractions(walletMapper);
        BDDMockito.verifyNoInteractions(walletMovementsRepository);
    }


}