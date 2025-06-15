package com.wallet.usecase;

import com.wallet.DTO.WalletRequestDTO;
import com.wallet.DTO.WalletResponseDTO;

public interface CreateWalletService {

    WalletResponseDTO create(WalletRequestDTO request);
}
