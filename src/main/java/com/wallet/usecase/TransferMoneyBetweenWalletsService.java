package com.wallet.usecase;

import com.wallet.DTO.TransferMoneyWalletRequestDTO;

public interface TransferMoneyBetweenWalletsService {

    void transfer(TransferMoneyWalletRequestDTO request);
}
