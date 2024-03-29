package com.kiura.api.controllers;

import com.kiura.api.dtos.JsonMessageDto;
import com.kiura.api.dtos.request.ContratoRequestDto;
import com.kiura.api.dtos.response.ContratoResponseDto;
import com.kiura.api.entities.Contrato;
import com.kiura.api.services.impl.ContratoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/kiura")
public class ContratoController {

    private final ContratoService contratoService;

    @Autowired
    public ContratoController(ContratoService contratoService) {
        this.contratoService = contratoService;
    }

    @GetMapping("api/contratos/{id}")
    @Secured({ "ADMIN", "NORMAL", "PROFESIONAL" })
    public ResponseEntity<ContratoResponseDto> obtenerContratoPorId (@PathVariable Long id)  {
        return new ResponseEntity<>(contratoService.buscarPorId(id), HttpStatus.OK);
    }

    @PostMapping("/api/contratos")
    @Secured({ "NORMAL" })
    public ResponseEntity<?> registrarContrato (@RequestBody @Valid ContratoRequestDto contratoRequestDto) {
        contratoService.crear(contratoRequestDto);
        return new ResponseEntity<>(new JsonMessageDto("Nuevo contrato registrado",HttpStatus.CREATED.value()), HttpStatus.CREATED);
    }

    @DeleteMapping("/api/contratos/{id}")
    @Secured({ "PROFESIONAL", "NORMAL" })
    public ResponseEntity<?> eliminarContratoPorId (@PathVariable Long id) {
        contratoService.borrarPorId(id);
        return new ResponseEntity<>(new JsonMessageDto("Contrato eliminado exitosamente",HttpStatus.OK.value()), HttpStatus.OK);
    }

    @GetMapping("/api/contratos")
    @Secured({"ADMIN", "NORMAL", "PROFESIONAL"})
    public ResponseEntity<List<ContratoResponseDto>> listarContratos () {
        return new ResponseEntity<>(contratoService.listarTodos(),HttpStatus.OK);
    }

    @PutMapping("/api/contratos")
    @Secured({ "PROFESIONAL", "NORMAL"})
    public ResponseEntity<?> actualizarContrato (@RequestBody @Valid ContratoRequestDto contratoRequestDto) {
        contratoService.actualizar(contratoRequestDto);
        return new ResponseEntity<>(new JsonMessageDto("Contrato actualizado exitosamente",HttpStatus.OK.value()), HttpStatus.OK);
    }

    @GetMapping("/api/contratos/usuarios/{id}")
    @Secured({"ADMIN", "NORMAL", "PROFESIONAL"})
    public ResponseEntity<List<ContratoResponseDto>> obtenerContratosPorUsuario(@PathVariable Long id) {
        return new ResponseEntity<>(contratoService.obtenerContratosPorUsuario(id), HttpStatus.OK);
    }

    /*@GetMapping("/reservas/bicicletas/{id}")
    public ResponseEntity<List<ReservaResponseDto>> obtenerReservasPorBicicleta(@PathVariable Long id) {
        return new ResponseEntity<>(reservaService.obtenerReservasPorBicicleta(id), HttpStatus.OK);
    }*/

    @GetMapping("/api/solicitudes-pendientes/{idProfesional}")
    @Secured({"ADMIN","PROFESIONAL"})
    public ResponseEntity<List<Contrato>> obtenerSolicitudesPendientes(@PathVariable Long idProfesional) {
        List<Contrato> solicitudesPendientes = contratoService.obtenerSolicitudesPendientes(idProfesional);
        return ResponseEntity.ok(solicitudesPendientes);
    }

    @PutMapping("/api/confirmar/{idContrato}")
    @Secured("PROFESIONAL")
    public ResponseEntity<?> confirmarSolicitudContratacion(@PathVariable Long idContrato) {
        contratoService.confirmarSolicitudContratacion(idContrato);
        //return ResponseEntity.ok().build();
        return new ResponseEntity<>(new JsonMessageDto("Contrato confirmado",HttpStatus.OK.value()), HttpStatus.OK);

    }

    @PutMapping("/api/rechazar/{idContrato}")
    @Secured("PROFESIONAL")
    public ResponseEntity<?> rechazarSolicitudContratacion(@PathVariable Long idContrato) {
        contratoService.rechazarSolicitudContratacion(idContrato);
        //return ResponseEntity.ok().build();
        return new ResponseEntity<>(new JsonMessageDto("Contrato rechazado",HttpStatus.OK.value()), HttpStatus.OK);

    }
}
