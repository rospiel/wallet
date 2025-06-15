package com.wallet.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawalRequestDTO {

    @NotNull(message = "Value must be informed")
    @Digits(integer = 11, fraction = 2, message = "The amount deposited must be a maximum of 99999999999,99")
    @Positive(message = "Withdrawal amount must be greater than zero")
    @JsonProperty("amount")
    @Schema(example = "500.89")
    private BigDecimal amount;

    @NotBlank(message = "cpf must be informed and can't be empty")
    @CPF(message = "cpf must be valid")
    @JsonProperty("cpf")
    @Schema(example = "37678774060")
    private String cpf;
}
