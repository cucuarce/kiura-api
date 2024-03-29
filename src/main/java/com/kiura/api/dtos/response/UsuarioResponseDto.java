package com.kiura.api.dtos.response;

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

    private List<Valoracion> valoraciones;
    private Double promedioPuntuacion;
    private Long cantidadValoraciones;
    private List<Contrato> contratosComoUsuarioNormal;
    private List<Contrato> contratosComoUsuarioProfesional;
    private Estado estadoAprobacion;

}
