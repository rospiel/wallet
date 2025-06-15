package com.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.DTO.WalletRequestDTO;
import com.wallet.usecase.BasicWalletService;
import com.wallet.usecase.CreateWalletService;
import com.wallet.usecase.GetBalanceWalletService;
import com.wallet.usecase.TransferMoneyBetweenWalletsService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(controllers = WalletController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BankSlipControllerTest {

    @MockitoBean
    private CreateWalletService createWalletService;

    @MockitoBean
    private GetBalanceWalletService getBalanceWalletService;

    @MockitoBean
    private BasicWalletService basicWalletService;

    @MockitoBean
    private TransferMoneyBetweenWalletsService transferMoneyBetweenWalletsService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @Order(1)
    void createInvalidCPF() throws Exception {
        WalletRequestDTO request = new WalletRequestDTO();


        MvcResult mvcResult = mockMvc.perform(post("/wallet/v1")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();

        Exception resolvedException = mvcResult.getResolvedException();
        Assertions.assertThat(resolvedException.getMessage()).contains("cpf must be informed and can't be empty");


        BDDMockito.verifyNoInteractions(createWalletService);

    }

}