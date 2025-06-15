package com.wallet.usecase.impl;

import com.wallet.DTO.WalletRequestDTO;
import com.wallet.DTO.WalletResponseDTO;
import com.wallet.entity.SystemParameterEntity;
import com.wallet.entity.WalletEntity;
import com.wallet.mapper.WalletMapper;
import com.wallet.repository.SystemParameterRepository;
import com.wallet.repository.WalletRepository;
import com.wallet.usecase.CreateWalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static com.wallet.utils.Constants.LAST_NUMBER_ACCOUNT;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateWalletUseCase implements CreateWalletService {

    private final SystemParameterRepository systemParameterRepository;
    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;

    @Override
    @Transactional
    public WalletResponseDTO create(WalletRequestDTO request) {
        Optional<WalletEntity> walletFound = walletRepository.findByCpf(request.getCpf());

        if (walletFound.isPresent()) {
            return walletMapper.toWalletResponseDTOBy(walletFound.get());
        }

        SystemParameterEntity lastNumberAccount = systemParameterRepository.findByName(LAST_NUMBER_ACCOUNT)
                .orElseThrow(() -> {
                    log.error("No name parameter found.: [{}]", LAST_NUMBER_ACCOUNT);
                    return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "System parameter not found");
                });

        WalletEntity walletCreated = walletMapper.toWalletEntityBy(request, lastNumberAccount.getData());
        walletRepository.save(walletCreated);
        lastNumberAccount.setData(walletCreated.getAccount());

        return walletMapper.toWalletResponseDTOBy(walletCreated);
    }
}
