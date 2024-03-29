package com.kiura.api.dtos.request;

import com.kiura.api.entities.Rol;
import lombok.Data;


@Data
public class UsuarioRequestDto {

    private Long id;

    private String nombre;

    private String apellido;

    private String email;

    private String password;

    private Rol rol;

    private String foto;

    private String documentoIdentidad;

    private String certificadosEstudios;

    private CategoriaRequestDto categoria;

    public UsuarioRequestDto(String nombre, String apellido, String email, String password, Rol rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    public UsuarioRequestDto(String nombre, String apellido, String email, String password, Rol rol, String foto, String documentoIdentidad, String certificadosEstudios, CategoriaRequestDto categoria) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.foto = foto;
        this.documentoIdentidad = documentoIdentidad;
        this.certificadosEstudios = certificadosEstudios;
        this.categoria = categoria;
    }

    public UsuarioRequestDto() {
    }
}
