package com.wallet.usecase;

import com.wallet.DTO.DepositRequestDTO;
import com.wallet.DTO.WithdrawalRequestDTO;

public interface BasicWalletService {

    void deposit(DepositRequestDTO request);
    void withdrawal(WithdrawalRequestDTO request);
}
