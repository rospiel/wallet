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
import com.wallet.usecase.TransferMoneyBetweenWalletsService;
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
public class TransferMoneyBetweenWalletsUseCase implements TransferMoneyBetweenWalletsService {

    private final WalletRepository walletRepository;
    private final WalletMovementsRepository walletMovementsRepository;
    private final WalletMapper walletMapper;
    private final GetTypeWalletMovementsService getTypeWalletMovementsService;

    @Override
    @Transactional
    public void transfer(TransferMoneyWalletRequestDTO request) {
        checkTargetAndSourceWallet(request.getTargetCpf(), request.getSourceCpf());

        WalletEntity sourceWallet = searchByWallet(request.getSourceCpf());

        checkBalance(sourceWallet, request.getAmount());

        WalletEntity targetWallet = searchByWallet(request.getTargetCpf());

        TypeWalletMovementsEntity typeWalletMovementsEntity = getTypeWalletMovementsService.get(TypeWalletMovementsEnum.TRANSFER_BETWEEN_ACCOUNTS);

        executeMovementTargetWallet(typeWalletMovementsEntity, request, targetWallet, sourceWallet);

        executeMovementSourceWallet(typeWalletMovementsEntity, request, targetWallet, sourceWallet);

    }

    private void executeMovementTargetWallet(TypeWalletMovementsEntity typeWalletMovementsEntity,
                                             TransferMoneyWalletRequestDTO request, WalletEntity targetWallet,
                                             WalletEntity sourceWallet) {

        BigDecimal balance = targetWallet.getBalance().add(request.getAmount());
        WalletMovementsEntity walletMovements = walletMapper.toWalletMovementsEntityBy(request.getAmount(), request.getSourceCpf(),
            typeWalletMovementsEntity);
        walletMovements.setAmountAfterOperation(balance);
        walletMovements.setTargetWallet(targetWallet);
        walletMovements.setSourceWallet(sourceWallet);

        targetWallet.setBalance(balance);

        walletMovementsRepository.save(walletMovements);
    }

    private void executeMovementSourceWallet(TypeWalletMovementsEntity typeWalletMovementsEntity,
                                             TransferMoneyWalletRequestDTO request, WalletEntity targetWallet,
                                             WalletEntity sourceWallet) {

        BigDecimal amount = request.getAmount().multiply(BigDecimal.valueOf(-1));
        BigDecimal balance = sourceWallet.getBalance().subtract(request.getAmount());

        WalletMovementsEntity walletMovements = walletMapper.toWalletMovementsEntityBy(amount, request.getSourceCpf(),
            typeWalletMovementsEntity);
        walletMovements.setAmountAfterOperation(balance);
        walletMovements.setTargetWallet(sourceWallet);
        walletMovements.setSourceWallet(targetWallet);

        sourceWallet.setBalance(balance);

        walletMovementsRepository.save(walletMovements);
    }

    private void checkTargetAndSourceWallet(String targetCpf, String sourceCpf) {
        if (targetCpf.equals(sourceCpf)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Destination and source accounts must not be the same");
        }
    }

    private void checkBalance(WalletEntity sourceWallet, BigDecimal amount) {
        if (sourceWallet.getBalance().compareTo(amount) < 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "No operation allowed");
        }
    }

    private WalletEntity searchByWallet(String cpf) {
        return walletRepository.findByCpfWithPessimisticWrite(cpf)
            .orElseThrow(() -> {
                log.error("No wallet was found with this CPF.: [{}]", cpf);
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found");
            });

    }
}
