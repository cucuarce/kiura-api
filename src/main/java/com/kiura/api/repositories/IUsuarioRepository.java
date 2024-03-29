package com.kiura.api.repositories;

import com.kiura.api.entities.Categoria;
import com.kiura.api.entities.Rol;
import com.kiura.api.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u WHERE u.email = ?1")
    Optional<Usuario> findByEmail(String email);

    @Query("SELECT u FROM Usuario u WHERE u.id = :id AND u.rol = :rol")
    Optional<Usuario> findByIdAndRol(Long id, Rol rol);

    List<Usuario> findByCategoria(Categoria categoria);

    List<Usuario> findByCategoriaIdAndRol(Long categoriaId, Rol rol);
}
