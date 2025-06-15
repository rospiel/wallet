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
import com.wallet.usecase.BasicWalletService;
import com.wallet.usecase.GetTypeWalletMovementsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
@Slf4j
public class BasicWalletUseCase implements BasicWalletService {

    private final WalletRepository walletRepository;
    private final WalletMovementsRepository walletMovementsRepository;
    private final WalletMapper walletMapper;
    private final GetTypeWalletMovementsService getTypeWalletMovementsService;

    @Override
    @Transactional
    public void deposit(DepositRequestDTO request) {
        WalletEntity walletFound = searchByWallet(request.getCpf());

        TypeWalletMovementsEntity typeWalletMovementsEntity = getTypeWalletMovementsService.get(TypeWalletMovementsEnum.DEPOSIT);

        BigDecimal balance = walletFound.getBalance().add(request.getAmount());
        WalletMovementsEntity target = walletMapper.toWalletMovementsEntityBy(request, walletFound, typeWalletMovementsEntity);
        target.setAmountAfterOperation(balance);
        walletFound.setBalance(balance);
        walletMovementsRepository.save(target);
    }

    @Override
    @Transactional
    public void withdrawal(WithdrawalRequestDTO request) {
        WalletEntity walletFound = searchByWallet(request.getCpf());

        TypeWalletMovementsEntity typeWalletMovementsEntity = getTypeWalletMovementsService.get(TypeWalletMovementsEnum.WITHDRAWAL);

        if (walletFound.getBalance().compareTo(request.getAmount()) < 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "No operation allowed");
        }

        BigDecimal balance = walletFound.getBalance().subtract(request.getAmount());
        WalletMovementsEntity target = walletMapper.toWalletMovementsEntityBy(request, walletFound, typeWalletMovementsEntity);
        target.setAmountAfterOperation(balance);
        walletFound.setBalance(balance);
        walletMovementsRepository.save(target);
    }

    private WalletEntity searchByWallet(String cpf) {
        return walletRepository.findByCpfWithPessimisticWrite(cpf)
            .orElseThrow(() -> {
                log.error("No wallet was found with this CPF.: [{}]", cpf);
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found");
            });

    }
}
