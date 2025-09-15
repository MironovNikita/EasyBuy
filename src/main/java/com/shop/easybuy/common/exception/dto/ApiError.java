package com.shop.easybuy.common.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiError {
    private String errorMessage;

    private int statusCode;

    private String time;
}
