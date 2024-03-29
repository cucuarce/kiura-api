package com.kiura.api.repositories;

import com.kiura.api.entities.Contrato;
import com.kiura.api.entities.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IContratoRepository extends JpaRepository<Contrato, Long> {
    List<Contrato> findByUsuarioProfesionalId(Long usuarioProfesionalId);

    List<Contrato> findByUsuarioNormalId(Long id);

    @Query("SELECT c FROM Contrato c WHERE c.usuarioProfesional.id = :idProfesional AND c.estado = :estado")
    List<Contrato> findSolicitudesPendientesByProfesionalId(Long idProfesional, Estado estado);

    @Query("SELECT c FROM Contrato c WHERE c.usuarioProfesional.id = ?1 AND ((c.fechaInicio <= ?2 AND c.fechaFin >= ?2) OR (c.fechaInicio <= ?3 AND c.fechaFin >= ?3))")
    Optional<Contrato> findByUsuarioProfesionalAndFechaInicioAndFechaFin(Long usuarioProfesionalId, LocalDate fechaInicio, LocalDate fechaFin);
}
