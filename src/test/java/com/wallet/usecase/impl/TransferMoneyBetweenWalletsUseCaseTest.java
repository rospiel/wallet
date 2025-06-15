package com.wallet.usecase.impl;

import com.wallet.DTO.TransferMoneyWalletRequestDTO;
import com.wallet.entity.TypeWalletMovementsEntity;
import com.wallet.entity.WalletEntity;
import com.wallet.entity.WalletMovementsEntity;
import com.wallet.enums.TypeWalletMovementsEnum;
import com.wallet.mapper.WalletMapper;
import com.wallet.repository.WalletMovementsRepository;
import com.wallet.repository.WalletRepository;
import com.wallet.usecase.GetTypeWalletMovementsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static com.wallet.mother.WalletMother.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
@Profile("test")
class TransferMoneyBetweenWalletsUseCaseTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletMovementsRepository walletMovementsRepository;

    @Mock
    private WalletMapper walletMapper;

    @Mock
    private GetTypeWalletMovementsService getTypeWalletMovementsService;

    @InjectMocks
    private TransferMoneyBetweenWalletsUseCase useCase;

    @Test
    void transferSuccess() {
        TransferMoneyWalletRequestDTO request = buildTransferMoneyWalletRequestDTO();

        WalletEntity sourceFound = buildWalletEntity();
        sourceFound.setBalance(sourceFound.getBalance().add(request.getAmount()));
        BigDecimal sourceFoundBalance = sourceFound.getBalance();
        BDDMockito.when(walletRepository.findByCpfWithPessimisticWrite(request.getSourceCpf())).thenReturn(Optional.of(sourceFound));

        WalletEntity targetFound = buildWalletEntity();
        targetFound.setBalance(targetFound.getBalance().add(request.getAmount()));
        BigDecimal targetFoundBalance = targetFound.getBalance();
        BDDMockito.when(walletRepository.findByCpfWithPessimisticWrite(request.getTargetCpf())).thenReturn(Optional.of(targetFound));

        TypeWalletMovementsEntity typeWalletMovementsEntity = buildTypeWalletMovementsEntity(TypeWalletMovementsEnum.TRANSFER_BETWEEN_ACCOUNTS);
        BDDMockito.when(getTypeWalletMovementsService.get(TypeWalletMovementsEnum.TRANSFER_BETWEEN_ACCOUNTS)).thenReturn(typeWalletMovementsEntity);

        WalletMovementsEntity targetWalletMovements = new WalletMovementsEntity();
        BDDMockito.when(walletMapper.toWalletMovementsEntityBy(request.getAmount(), request.getSourceCpf(), typeWalletMovementsEntity))
                .thenReturn(targetWalletMovements);

        BigDecimal negativeAmount = request.getAmount().multiply(BigDecimal.valueOf(-1));
        WalletMovementsEntity sourceWalletMovements = new WalletMovementsEntity();
        BDDMockito.when(walletMapper
                        .toWalletMovementsEntityBy(negativeAmount, request.getSourceCpf(), typeWalletMovementsEntity))
                        .thenReturn(sourceWalletMovements);



        useCase.transfer(request);
        BigDecimal targetBalance = targetFoundBalance.add(request.getAmount());
        Assertions.assertEquals(targetBalance, targetWalletMovements.getAmountAfterOperation());
        Assertions.assertEquals(targetFound, targetWalletMovements.getTargetWallet());
        Assertions.assertEquals(sourceFound, targetWalletMovements.getSourceWallet());
        Assertions.assertEquals(targetBalance, targetFound.getBalance());

        BigDecimal sourceBalance = sourceFoundBalance.subtract(request.getAmount());
        Assertions.assertEquals(sourceBalance, sourceWalletMovements.getAmountAfterOperation());
        Assertions.assertEquals(sourceFound, sourceWalletMovements.getTargetWallet());
        Assertions.assertEquals(targetFound, sourceWalletMovements.getSourceWallet());
        Assertions.assertEquals(sourceBalance, sourceFound.getBalance());

        BDDMockito.verify(walletRepository, Mockito.times(2)).findByCpfWithPessimisticWrite(anyString());
        BDDMockito.verify(getTypeWalletMovementsService).get(any(TypeWalletMovementsEnum.class));
        BDDMockito.verify(walletMapper, Mockito.times(2)).toWalletMovementsEntityBy(any(BigDecimal.class), any(String.class), any(TypeWalletMovementsEntity.class));
        BDDMockito.verify(walletMovementsRepository).save(targetWalletMovements);
        BDDMockito.verify(walletMovementsRepository).save(sourceWalletMovements);

    }

    @Test
    void transferSourceAndTargetSameCPF() {
        TransferMoneyWalletRequestDTO request = buildTransferMoneyWalletRequestDTO();
        request.setTargetCpf(request.getSourceCpf());

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> useCase.transfer(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("422 UNPROCESSABLE_ENTITY \"Destination and source accounts must not be the same\"");

        BDDMockito.verifyNoInteractions(walletRepository);
        BDDMockito.verifyNoInteractions(getTypeWalletMovementsService);
        BDDMockito.verifyNoInteractions(walletMapper);
        BDDMockito.verifyNoInteractions(walletMovementsRepository);
    }

    @Test
    void transferSourceWalletNotFound() {
        TransferMoneyWalletRequestDTO request = buildTransferMoneyWalletRequestDTO();

        BDDMockito.when(walletRepository.findByCpfWithPessimisticWrite(request.getSourceCpf())).thenReturn(Optional.empty());

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> useCase.transfer(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"Wallet not found\"");

        BDDMockito.verify(walletRepository).findByCpfWithPessimisticWrite(anyString());
        BDDMockito.verifyNoMoreInteractions(walletRepository);
        BDDMockito.verifyNoInteractions(getTypeWalletMovementsService);
        BDDMockito.verifyNoInteractions(walletMapper);
        BDDMockito.verifyNoInteractions(walletMovementsRepository);
    }

    @Test
    void transferTargetWalletNotFound() {
        TransferMoneyWalletRequestDTO request = buildTransferMoneyWalletRequestDTO();

        WalletEntity sourceFound = buildWalletEntity();
        sourceFound.setBalance(sourceFound.getBalance().add(request.getAmount()));
        BDDMockito.when(walletRepository.findByCpfWithPessimisticWrite(request.getSourceCpf())).thenReturn(Optional.of(sourceFound));

        BDDMockito.when(walletRepository.findByCpfWithPessimisticWrite(request.getTargetCpf())).thenReturn(Optional.empty());

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> useCase.transfer(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"Wallet not found\"");

        BDDMockito.verify(walletRepository, Mockito.times(2)).findByCpfWithPessimisticWrite(anyString());
        BDDMockito.verifyNoInteractions(getTypeWalletMovementsService);
        BDDMockito.verifyNoInteractions(walletMapper);
        BDDMockito.verifyNoInteractions(walletMovementsRepository);
    }

    @Test
    void transferInsufficientBalance() {
        TransferMoneyWalletRequestDTO request = buildTransferMoneyWalletRequestDTO();

        WalletEntity sourceFound = buildWalletEntity();
        sourceFound.setBalance(BigDecimal.ZERO);
        BDDMockito.when(walletRepository.findByCpfWithPessimisticWrite(request.getSourceCpf())).thenReturn(Optional.of(sourceFound));

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> useCase.transfer(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("422 UNPROCESSABLE_ENTITY \"No operation allowed\"");

        BDDMockito.verify(walletRepository).findByCpfWithPessimisticWrite(anyString());
        BDDMockito.verifyNoMoreInteractions(walletRepository);
        BDDMockito.verifyNoInteractions(getTypeWalletMovementsService);
        BDDMockito.verifyNoInteractions(walletMapper);
        BDDMockito.verifyNoInteractions(walletMovementsRepository);
    }
}