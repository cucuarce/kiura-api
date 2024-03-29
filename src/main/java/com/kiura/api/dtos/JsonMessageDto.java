package com.kiura.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JsonMessageDto {

    private String message;

    private Integer statusCode;


}
