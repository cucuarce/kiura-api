package com.kiura.api.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CategoriaResponseDto {

    private Long id;
    private String nombre;
    private String descripcion;

}
