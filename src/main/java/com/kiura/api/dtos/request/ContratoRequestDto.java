package com.kiura.api.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kiura.api.entities.Estado;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Validated
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContratoRequestDto {

    private Long id;

    private UsuarioRequestDto usuarioNormal;

    private UsuarioRequestDto usuarioProfesional;

    @NotNull(message = "La fecha de inicio no puede ser nula")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin no puede ser nula")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate fechaFin;

    private Estado estado;

}
