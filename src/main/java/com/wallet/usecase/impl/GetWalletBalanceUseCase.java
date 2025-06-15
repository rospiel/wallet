package com.wallet.usecase.impl;

import com.wallet.DTO.WalletBalanceResponseDTO;
import com.wallet.entity.WalletEntity;
import com.wallet.mapper.WalletMapper;
import com.wallet.repository.WalletMovementsRepository;
import com.wallet.repository.WalletRepository;
import com.wallet.usecase.GetBalanceWalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetWalletBalanceUseCase implements GetBalanceWalletService {

    private final WalletRepository walletRepository;
    private final WalletMovementsRepository walletMovementsRepository;
    private final WalletMapper walletMapper;

    @Override
    public WalletBalanceResponseDTO get(String cpf) {
        WalletEntity walletFound = walletRepository.findByCpf(cpf)
            .orElseThrow(() -> {
                log.error("No wallet was found with this CPF.: [{}]", cpf);
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found");
            });

        return walletMapper.toWalletBalanceResponseDTOBy(walletFound.getBalance());
    }

    @Override
    public WalletBalanceResponseDTO getByPeriod(String cpf, String date) {
        BigDecimal balance = walletMovementsRepository.findBalanceBy(cpf, convertDate(date));

        return walletMapper.toWalletBalanceResponseDTOBy(isNull(balance) ? BigDecimal.ZERO : balance);
    }

    private LocalDateTime convertDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            return LocalDateTime.parse(date, formatter);
        } catch (Exception error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "date must must be informed and in the format yyyy-mm-dd hh:mm:ss. Ex.: 2025-06-14 15:57:59");
        }
    }
}
