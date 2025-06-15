package com.wallet.controller;


import com.wallet.DTO.*;
import com.wallet.enums.TypeWalletMovementsEnum;
import com.wallet.usecase.CreateWalletService;
import com.wallet.usecase.BasicWalletService;
import com.wallet.usecase.GetBalanceWalletService;
import com.wallet.usecase.TransferMoneyBetweenWalletsService;
import com.wallet.utils.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("wallet")
@RequiredArgsConstructor
@Tag(name = "Wallet")
@Validated
public class WalletController {

    private final CreateWalletService createWalletService;
    private final GetBalanceWalletService getBalanceWalletService;
    private final BasicWalletService basicWalletService;
    private final TransferMoneyBetweenWalletsService transferMoneyBetweenWalletsService;


    @Operation(summary = "Create a wallet", description = "Create a wallet to manager money", responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "422", description = "It was not possible to process the request", content = @Content(schema = @Schema))
    })
    @PostMapping(value = Constants.VERSION_1, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public WalletResponseDTO create(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Properties of a wallet", required = true) @Valid @RequestBody WalletRequestDTO request) {
        return createWalletService.create(request);
    }

    @Operation(summary = "Deposit in wallet", description = "Add funds to the account", responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "404", description = "It was not possible to process the request", content = @Content(schema = @Schema))
    })
    @PostMapping(value = Constants.VERSION_1 + "/deposit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void deposit(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Properties of a deposit wallet", required = true) @Valid @RequestBody DepositRequestDTO request) {
        basicWalletService.deposit(request);
    }

    @Operation(summary = "Withdrawal in wallet", description = "Remove funds to the account", responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "404", description = "It was not possible to process the request", content = @Content(schema = @Schema))
    })
    @PostMapping(value = Constants.VERSION_1 + "/withdrawal", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void withdrawal(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Properties of a withdrawal wallet", required = true) @Valid @RequestBody WithdrawalRequestDTO request) {
        basicWalletService.withdrawal(request);
    }

    @Operation(summary = "Transfer between wallets", description = "Remove funds to the account to another", responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "404", description = "It was not possible to process the request", content = @Content(schema = @Schema))
    })
    @PostMapping(value = Constants.VERSION_1 + "/transfer", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void transfer(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Properties of a transfer between wallets", required = true) @Valid @RequestBody TransferMoneyWalletRequestDTO request) {
        transferMoneyBetweenWalletsService.transfer(request);
    }

    @Operation(summary = "Get wallet balance", description = "Search for wallet balance by CPF", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Wallet not found", content = @Content(schema = @Schema))
    })
    @GetMapping(value = Constants.VERSION_1 + "/balance/document/{cpf}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public WalletBalanceResponseDTO getWalletBalance(@Parameter(description = "Document associated with the wallet", example = "37678774060", required = true) @CPF(message = "cpf must be valid") @PathVariable String cpf) {
        return getBalanceWalletService.get(cpf);
    }

    @Operation(summary = "Get wallet balance", description = "Search for wallet balance by CPF and period", responses = {
            @ApiResponse(responseCode = "200")
    })
    @GetMapping(value = Constants.VERSION_1 + "/balanceByPeriod/document/{cpf}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public WalletBalanceResponseDTO getWalletBalanceByPeriod(@Parameter(description = "Document associated with the wallet", example = "37678774060", required = true) @CPF(message = "cpf must be valid") @PathVariable String cpf,
                                                             @Parameter(description = "Date to search", example = "2025-06-14 14:59:59", required = true) @RequestParam String date) {
        return getBalanceWalletService.getByPeriod(cpf, date);
    }

}
