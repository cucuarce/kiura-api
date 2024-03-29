package com.kiura.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "usuarios", uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})})
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(nullable = true)
    private String foto;

    @Column(nullable = true)
    private String documentoIdentidad;

    @Column(nullable = true)
    private String certificadosEstudios;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "categoria_id", nullable = true)
    private Categoria categoria;

    @OneToMany(mappedBy = "usuarioProfesional", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Valoracion> valoraciones = new ArrayList<>();

    @Column(name = "promedio_puntuacion")
    private Double promedioPuntuacion;

    @Column(name = "cantidad_valoraciones")
    private Long cantidadValoraciones;

    @OneToMany(mappedBy = "usuarioNormal", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Contrato> contratosComoUsuarioNormal = new ArrayList<>();

    @OneToMany(mappedBy = "usuarioProfesional", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Contrato> contratosComoUsuarioProfesional = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Estado estadoAprobacion;

    public Usuario(String nombre, String apellido, String email, String password, Rol rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    public Usuario(String nombre, String apellido, String email, String password, Rol rol, String foto, String documentoIdentidad, String certificadosEstudios, Categoria categoria) {
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

    public Usuario() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(rol.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
