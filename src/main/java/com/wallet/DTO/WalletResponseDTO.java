package com.wallet.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletResponseDTO {

    @JsonProperty("account")
    private String account;

    @JsonProperty("agency")
    private String agency;

}
