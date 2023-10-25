package com.bank.javabankapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankResponse {


    @Schema(
            name = "Response Code"
    )
    private String responseCode;

    @Schema(
            name = "Response Message"
    )
    private String responseMessage;

    @Schema(
            name = "Account Info"
    )
    private AccountInfo accountInfo;
}
