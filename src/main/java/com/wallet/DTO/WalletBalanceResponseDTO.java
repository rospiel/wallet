package com.wallet.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletBalanceResponseDTO {

    @JsonProperty("balance")
    private String balance;
}
