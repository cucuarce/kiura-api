package com.kiura.api.dtos.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.validation.annotation.Validated;

@Validated
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ValoracionRequestDto {

    private Long id;

    private ContratoRequestDto contrato;

    @NotNull(message = "La puntuación no puede ser nula" )
    @Min(1)
    @Max(5)
    private int puntuacion;

    @Size(max = 2000)
    private String comentario;

    private UsuarioRequestDto usuarioProfesional;

}
