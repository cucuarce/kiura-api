package com.kiura.api.repositories;

import com.kiura.api.entities.Contrato;
import com.kiura.api.entities.Usuario;
import com.kiura.api.entities.Valoracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IValoracionRepository extends JpaRepository<Valoracion, Long> {
    List<Valoracion> findByUsuarioProfesionalId(Long usuarioprofesionalId);
    boolean existsByUsuarioNormalAndContrato(Usuario usuarioNormal, Contrato contrato);

}
