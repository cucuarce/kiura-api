package com.kiura.api.security.auth;

import com.kiura.api.entities.Categoria;
import com.kiura.api.entities.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {

    private String nombre;

    private String apellido;

    private String email;

    private String password;

    private Rol rol;

    private String foto;

    private String documentoIdentidad;

    private String certificadosEstudios;

    private Long categoriaId;

}
