package com.kiura.api.dtos.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kiura.api.entities.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UsuarioResponseDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private Rol rol;
    private String foto;
    private String documentoIdentidad;
    private String certificadosEstudios;
    private CategoriaResponseDto categoria;

    @JsonIgnore
    private List<Valoracion> valoraciones;
    @JsonIgnore
    private Double promedioPuntuacion;
    @JsonIgnore
    private Long cantidadValoraciones;
    @JsonIgnore
    private List<Contrato> contratosComoUsuarioNormal;
    @JsonIgnore
    private List<Contrato> contratosComoUsuarioProfesional;
    @JsonIgnore
    private Estado estadoAprobacion;

}
