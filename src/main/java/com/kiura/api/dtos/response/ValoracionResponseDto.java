package com.kiura.api.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ValoracionResponseDto {
    private Long id;
    private UsuarioResponseDto usuarioProfesional;
    private int puntuacion;
    private String comentario;
    private LocalDate fecha;
}
