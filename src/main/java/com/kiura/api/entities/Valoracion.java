package com.kiura.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "valoraciones")
public class Valoracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "valoracion_id")
    private Long id;

    @Column
    private int puntuacion;

    @ManyToOne
    @JoinColumn(name = "usuario_normal_id", nullable = false)
    private Usuario usuarioNormal;

    @Column
    private String comentario;

    @Column
    private LocalDate fecha;

    @ManyToOne()
    @JsonIgnore
    @JoinColumn(name = "usuario_profesional_id", nullable = false)
    private Usuario usuarioProfesional;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "contrato_id")
    private Contrato contrato;

    @PrePersist
    protected void onCreate() {
        fecha = LocalDate.now();
    }

    public Valoracion(Long id, int puntuacion, Usuario usuarioProfesional, String comentario, LocalDate fecha) {
        this.id = id;
        this.puntuacion = puntuacion;
        this.usuarioProfesional = usuarioProfesional;
        this.comentario = comentario;
        this.fecha = fecha;
    }
}
