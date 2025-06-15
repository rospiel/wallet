package com.wallet.usecase.impl;

import com.wallet.DTO.WalletRequestDTO;
import com.wallet.DTO.WalletResponseDTO;
import com.wallet.entity.SystemParameterEntity;
import com.wallet.entity.WalletEntity;
import com.wallet.mapper.WalletMapper;
import com.wallet.repository.SystemParameterRepository;
import com.wallet.repository.WalletRepository;
import com.wallet.usecase.impl.CreateWalletUseCase;
import com.wallet.utils.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static com.wallet.mother.WalletMother.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
@Profile("test")
class CreateWalletUseCaseTest {

    @Mock
    private SystemParameterRepository systemParameterRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletMapper walletMapper;

    @InjectMocks
    private CreateWalletUseCase useCase;

    @Test
    void createSuccess() {
        WalletRequestDTO request = buildWalletRequestDTO();

        BDDMockito.when(walletRepository.findByCpf(request.getCpf())).thenReturn(Optional.empty());

        SystemParameterEntity systemParameterEntity = buildSystemParameterEntity();
        BDDMockito.when(systemParameterRepository.findByName(Constants.LAST_NUMBER_ACCOUNT)).thenReturn(Optional.of(systemParameterEntity));

        WalletEntity walletEntity = new WalletEntity();
        walletEntity.setAccount("1");
        BDDMockito.when(walletMapper.toWalletEntityBy(request, systemParameterEntity.getData()))
            .thenReturn(walletEntity);

        WalletResponseDTO walletResponseDTO = buildWalletResponseDTO();
        BDDMockito.when(walletMapper.toWalletResponseDTOBy(walletEntity))
                .thenReturn(walletResponseDTO);

        useCase.create(request);
        Assertions.assertEquals(walletEntity.getAccount(), systemParameterEntity.getData());

        BDDMockito.verify(walletRepository).findByCpf(anyString());
        BDDMockito.verify(systemParameterRepository).findByName(anyString());
        BDDMockito.verify(walletMapper).toWalletEntityBy(any(WalletRequestDTO.class), anyString());
        BDDMockito.verify(walletRepository).save(walletEntity);
        BDDMockito.verify(walletMapper).toWalletResponseDTOBy(any(WalletEntity.class));
    }

    @Test
    void createWalletAlreadyExists() {
        WalletRequestDTO request = buildWalletRequestDTO();

        WalletEntity walletEntity = buildWalletEntity();
        BDDMockito.when(walletRepository.findByCpf(request.getCpf())).thenReturn(Optional.of(walletEntity));

        WalletResponseDTO walletResponseDTO = buildWalletResponseDTO();
        BDDMockito.when(walletMapper.toWalletResponseDTOBy(walletEntity))
                .thenReturn(walletResponseDTO);

        useCase.create(request);

        BDDMockito.verify(walletRepository).findByCpf(anyString());
        BDDMockito.verifyNoInteractions(systemParameterRepository);
        BDDMockito.verify(walletMapper, Mockito.times(0)).toWalletEntityBy(any(WalletRequestDTO.class), anyString());
        BDDMockito.verifyNoMoreInteractions(walletRepository);
        BDDMockito.verify(walletMapper).toWalletResponseDTOBy(any(WalletEntity.class));
    }

    @Test
    void createSystemParameterEntityNotFound() {
        WalletRequestDTO request = buildWalletRequestDTO();

        BDDMockito.when(walletRepository.findByCpf(request.getCpf())).thenReturn(Optional.empty());

        BDDMockito.when(systemParameterRepository.findByName(Constants.LAST_NUMBER_ACCOUNT)).thenReturn(Optional.empty());

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> useCase.create(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("500 INTERNAL_SERVER_ERROR \"System parameter not found\"");


        BDDMockito.verify(walletRepository).findByCpf(anyString());
        BDDMockito.verify(systemParameterRepository).findByName(anyString());
        BDDMockito.verifyNoInteractions(walletMapper);
        BDDMockito.verifyNoMoreInteractions(walletRepository);
    }
}