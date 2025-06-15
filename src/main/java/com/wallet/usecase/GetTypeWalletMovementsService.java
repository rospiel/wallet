package com.wallet.usecase;

import com.wallet.entity.TypeWalletMovementsEntity;
import com.wallet.enums.TypeWalletMovementsEnum;

public interface GetTypeWalletMovementsService {

    TypeWalletMovementsEntity get(TypeWalletMovementsEnum typeWalletMovements);
}
