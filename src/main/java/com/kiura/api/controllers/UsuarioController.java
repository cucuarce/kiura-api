package com.kiura.api.controllers;

import com.kiura.api.dtos.JsonMessageDto;
import com.kiura.api.dtos.request.UsuarioRequestDto;
import com.kiura.api.dtos.response.UsuarioResponseDto;
import com.kiura.api.entities.Rol;
import com.kiura.api.entities.Usuario;
import com.kiura.api.services.impl.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/kiura/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/{id}")
    @Secured({ "ADMIN", "NORMAL", "SOPORTE", "PROFESIONAL" })
    public ResponseEntity<UsuarioResponseDto> obtenerUsuarioPorId (@PathVariable Long id) {
        return new ResponseEntity<>(usuarioService.buscarPorId(id), HttpStatus.OK);
    }

    @PostMapping
    @Secured("ADMIN")
    public ResponseEntity<?> registrarUsuario (@RequestBody UsuarioRequestDto usuarioRequestDto){
        usuarioService.crear(usuarioRequestDto);
        return new ResponseEntity<>(new JsonMessageDto("Nuevo usuario registrado",HttpStatus.CREATED.value()), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Secured({ "ADMIN", "NORMAL", "SOPORTE", "PROFESIONAL" })
    public ResponseEntity<?> eliminarUsuarioPorId (@PathVariable Long id){
        usuarioService.borrarPorId(id);
        return new ResponseEntity<>(new JsonMessageDto("Usuario eliminado exitosamente",HttpStatus.OK.value()), HttpStatus.OK);
    }

    @GetMapping
    @Secured("ADMIN")
    public ResponseEntity<List<UsuarioResponseDto>> listarUsuarios (){
        return new ResponseEntity<>(usuarioService.listarTodos(),HttpStatus.OK);
    }

    @PutMapping
    @Secured({ "ADMIN", "USER", "SOPORTE", "PROFESIONAL" })
    public ResponseEntity<?> actualizarUsuario (@RequestBody UsuarioRequestDto usuarioRequestDto){
        usuarioService.actualizar(usuarioRequestDto);
        return new ResponseEntity<>(new JsonMessageDto("Usuario actualizado exitosamente",HttpStatus.OK.value()), HttpStatus.OK);
    }

    /**
     * Ejemplo: bike-me-now/api/usuarios/{id}/cambiar-rol?rol=ADMIN
     **/
    @PutMapping("/{usuarioId}/cambiar-rol")
    @Secured("ADMIN")
    public ResponseEntity<?> cambiarRolUsuario(@PathVariable Long usuarioId, @RequestParam("rol") String nuevoRol) {
        Rol rol = convertirStringARol(nuevoRol);
        usuarioService.cambiarRol(usuarioId, rol);
        return new ResponseEntity<>(new JsonMessageDto("Rol de usuario cambiado exitosamente", HttpStatus.OK.value()), HttpStatus.OK);
    }

    @GetMapping("/profesionales/{categoriaId}")
    @Secured({"ADMIN", "NORMAL"})
    public ResponseEntity<List<Usuario>> buscarProfesionalesPorCategoria(@PathVariable Long categoriaId) {
        List<Usuario> profesionales = usuarioService.buscarProfesionalesPorCategoria(categoriaId);
        if (profesionales.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(profesionales, HttpStatus.OK);
    }

    private Rol convertirStringARol(String nuevoRol) {
        if ("ADMIN".equalsIgnoreCase(nuevoRol)) {
            return Rol.ADMIN;
        } else if ("NORMAL".equalsIgnoreCase(nuevoRol)) {
            return Rol.NORMAL;
        } else if ("SOPORTE".equalsIgnoreCase(nuevoRol)) {
            return Rol.SOPORTE;
        } else if ("PROFESIONAL".equalsIgnoreCase(nuevoRol)) {
            return Rol.PROFESIONAL;
        } else {
            throw new IllegalArgumentException("Rol no válido: " + nuevoRol);
        }
    }

    @PutMapping("/aprobar-profesional/{id}")
    @Secured("SOPORTE")
    public ResponseEntity<?> aprobarProfesional(@PathVariable Long id) {
        Usuario usuario = usuarioService.aprobarProfesional(id);
        return ResponseEntity.ok().body("Profesional aprobado correctamente");
    }

    @Secured("SOPORTE")
    @PutMapping("/rechazar-profesional/{id}")
    public ResponseEntity<?> rechazarProfesional(@PathVariable Long id) {
        Usuario usuario = usuarioService.rechazarProfesional(id);
        return ResponseEntity.ok().body("Profesional rechazado");
    }
}
