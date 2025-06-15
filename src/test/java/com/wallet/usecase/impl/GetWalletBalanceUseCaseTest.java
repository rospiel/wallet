package com.wallet.usecase.impl;

import com.wallet.DTO.WalletBalanceResponseDTO;
import com.wallet.entity.WalletEntity;
import com.wallet.mapper.WalletMapper;
import com.wallet.repository.WalletMovementsRepository;
import com.wallet.repository.WalletRepository;
import com.wallet.usecase.impl.GetWalletBalanceUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.wallet.mother.WalletMother.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
@Profile("test")
class GetWalletBalanceUseCaseTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletMovementsRepository walletMovementsRepository;

    @Mock
    private WalletMapper walletMapper;

    @InjectMocks
    private GetWalletBalanceUseCase useCase;

    @Test
    void getSuccess() {
        WalletEntity walletFound = buildWalletEntity();
        BDDMockito.when(walletRepository.findByCpf(CPF)).thenReturn(Optional.of(walletFound));

        WalletBalanceResponseDTO walletBalanceResponseDTO = buildWalletBalanceResponseDTO();
        BDDMockito.when(walletMapper.toWalletBalanceResponseDTOBy(walletFound.getBalance()))
            .thenReturn(walletBalanceResponseDTO);

        WalletBalanceResponseDTO response = useCase.get(CPF);
        Assertions.assertNotNull(response);

        BDDMockito.verify(walletRepository).findByCpf(anyString());
        BDDMockito.verify(walletMapper).toWalletBalanceResponseDTOBy(any(BigDecimal.class));
    }

    @Test
    void getWalletNotFound() {
        BDDMockito.when(walletRepository.findByCpf(CPF)).thenReturn(Optional.empty());

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> useCase.get(CPF))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"Wallet not found\"");

        BDDMockito.verify(walletRepository).findByCpf(anyString());
        BDDMockito.verifyNoInteractions(walletMapper);
    }

    @Test
    void getByPeriodSuccess() {
        String date = "2025-06-14 14:59:59";

        BigDecimal balanceFound = buildMonetary();
        BDDMockito.when(walletMovementsRepository.findBalanceBy(eq(CPF), any(LocalDateTime.class))).thenReturn(balanceFound);

        WalletBalanceResponseDTO walletBalanceResponseDTO = buildWalletBalanceResponseDTO();
        BDDMockito.when(walletMapper.toWalletBalanceResponseDTOBy(balanceFound))
                .thenReturn(walletBalanceResponseDTO);

        WalletBalanceResponseDTO response = useCase.getByPeriod(CPF, date);

        BDDMockito.verify(walletMovementsRepository).findBalanceBy(anyString(), any(LocalDateTime.class));
        ArgumentCaptor<BigDecimal> acBalance = ArgumentCaptor.forClass(BigDecimal.class);
        BDDMockito.verify(walletMapper).toWalletBalanceResponseDTOBy(acBalance.capture());
        Assertions.assertEquals(balanceFound, acBalance.getValue());
    }

    @Test
    void getByPeriodNotFound() {
        String date = "2025-06-14 14:59:59";

        WalletBalanceResponseDTO walletBalanceResponseDTO = buildWalletBalanceResponseDTO();
        BDDMockito.when(walletMapper.toWalletBalanceResponseDTOBy(BigDecimal.ZERO))
                .thenReturn(walletBalanceResponseDTO);

        WalletBalanceResponseDTO response = useCase.getByPeriod(CPF, date);

        BDDMockito.verify(walletMovementsRepository).findBalanceBy(eq(CPF), any(LocalDateTime.class));
        ArgumentCaptor<BigDecimal> acBalance = ArgumentCaptor.forClass(BigDecimal.class);
        BDDMockito.verify(walletMapper).toWalletBalanceResponseDTOBy(acBalance.capture());
        Assertions.assertEquals(BigDecimal.ZERO, acBalance.getValue());
    }

    @Test
    void getByPeriodInvalidDate() {
        String invalidDate = "2025-15-14 14:59:59";

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> useCase.getByPeriod(CPF, invalidDate))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("400 BAD_REQUEST \"date must must be informed and in the format yyyy-mm-dd hh:mm:ss. Ex.: 2025-06-14 15:57:59\"");

        BDDMockito.verifyNoInteractions(walletMovementsRepository);
        BDDMockito.verifyNoInteractions(walletMapper);
    }
}