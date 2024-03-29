package com.kiura.api.security.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {

    @NotNull(message = "El email no puede ser nulo" )
    @NotBlank(message = "El email no puede estar vacio" )
    private String email;

    @NotNull(message = "La contraseña no puede ser nula" )
    @NotBlank(message = "La contraseña no puede estar vacia" )
    private String password;
}
