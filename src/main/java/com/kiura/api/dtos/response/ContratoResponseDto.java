package com.kiura.api.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kiura.api.entities.Estado;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ContratoResponseDto {

    private Long id;
    private UsuarioResponseDto usuarioNormal;
    private UsuarioResponseDto usuarioProfesional;
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate fechaInicio;
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate fechaFin;
    private boolean valoracion;
    private Estado estado;

}
