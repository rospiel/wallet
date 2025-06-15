package com.wallet.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletBalanceRequestDTO {

    @NotBlank(message = "cpf must be informed and can't be empty")
    @CPF(message = "cpf must be valid")
    @JsonProperty("cpf")
    @Schema(example = "37678774060")
    private String cpf;

}
