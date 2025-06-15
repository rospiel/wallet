package com.wallet.usecase.impl;

import com.wallet.entity.TypeWalletMovementsEntity;
import com.wallet.enums.TypeWalletMovementsEnum;
import com.wallet.repository.TypeWalletMovementsRepository;
import com.wallet.usecase.GetTypeWalletMovementsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
@Slf4j
public class GetTypeWalletMovementsUseCase implements GetTypeWalletMovementsService {

    private final TypeWalletMovementsRepository typeWalletMovementsRepository;

    @Override
    @Cacheable(value = "typeWalletMovements")
    public TypeWalletMovementsEntity get(TypeWalletMovementsEnum typeWalletMovements) {
        return typeWalletMovementsRepository.findByName(typeWalletMovements.name())
                    .orElseThrow(() -> {
                        log.error("No type wallet movements found.: [{}]", typeWalletMovements.name());
                        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Type wallet movements not found");
                    });
    }

    @CacheEvict(value = "typeWalletMovements", allEntries = true)
    @Scheduled(fixedRateString = "${caching.spring.typeWalletMovementsTTL}")
    public void emptyTypeWalletMovementsCache() {
        log.info("emptying typeWalletMovements cache");
    }
}
