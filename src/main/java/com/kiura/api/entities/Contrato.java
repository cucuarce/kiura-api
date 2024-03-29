package com.kiura.api.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contratos")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contrato_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_normal_id")
    private Usuario usuarioNormal;

    @ManyToOne
    @JoinColumn(name = "usuario_profesional_id")
    private Usuario usuarioProfesional;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate fechaInicio;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate fechaFin;

    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true) //cambie esto por cascade.all y orphan true
    private List<Valoracion> valoraciones;

    @Column
    private boolean valoracion = false;

    @Column
    @Enumerated(EnumType.STRING)
    private Estado estado;

}
