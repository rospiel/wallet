package com.wallet.DTO;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FieldErrorDto {

    private String name;
    private String userMessage;
}
