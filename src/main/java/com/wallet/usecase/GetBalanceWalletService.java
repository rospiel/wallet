package com.wallet.usecase;

import com.wallet.DTO.WalletBalanceResponseDTO;

import java.time.LocalDateTime;

public interface GetBalanceWalletService {

    WalletBalanceResponseDTO get(String cpf);
    WalletBalanceResponseDTO getByPeriod(String cpf, String date);
}
