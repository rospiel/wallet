package com.wallet.usecase.impl;

import com.wallet.entity.TypeWalletMovementsEntity;
import com.wallet.enums.TypeWalletMovementsEnum;
import com.wallet.repository.TypeWalletMovementsRepository;
import com.wallet.usecase.impl.GetTypeWalletMovementsUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
@Profile("test")
class GetTypeWalletMovementsUseCaseTest {

    @Mock
    private TypeWalletMovementsRepository typeWalletMovementsRepository;

    @InjectMocks
    private GetTypeWalletMovementsUseCase useCase;

    @Test
    void getSuccess() {
        TypeWalletMovementsEntity typeWalletMovementsEntity = new TypeWalletMovementsEntity();
        TypeWalletMovementsEnum typeWalletMovements = TypeWalletMovementsEnum.DEPOSIT;
        BDDMockito.when(typeWalletMovementsRepository.findByName(typeWalletMovements.name())).thenReturn(Optional.of(typeWalletMovementsEntity));

        TypeWalletMovementsEntity typeWalletMovementsFound = useCase.get(typeWalletMovements);
        Assertions.assertNotNull(typeWalletMovementsFound);

        BDDMockito.verify(typeWalletMovementsRepository).findByName(anyString());
    }

    @Test
    void getTypeWalletMovementsNotFound() {
        TypeWalletMovementsEnum typeWalletMovements = TypeWalletMovementsEnum.DEPOSIT;
        BDDMockito.when(typeWalletMovementsRepository.findByName(typeWalletMovements.name()))
            .thenReturn(Optional.empty());

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> useCase.get(typeWalletMovements))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("500 INTERNAL_SERVER_ERROR \"Type wallet movements not found\"");

        BDDMockito.verify(typeWalletMovementsRepository).findByName(anyString());
    }
}