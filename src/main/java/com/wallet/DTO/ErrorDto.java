package com.wallet.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
public class ErrorDto {

    private List<FieldErrorDto> fields;
    private String details;
    private LocalDateTime dateTime = LocalDateTime.now();
}
