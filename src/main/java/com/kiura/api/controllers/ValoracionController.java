package com.kiura.api.controllers;

import com.kiura.api.dtos.JsonMessageDto;
import com.kiura.api.dtos.request.ValoracionRequestDto;
import com.kiura.api.dtos.response.ValoracionResponseDto;
import com.kiura.api.services.impl.ValoracionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/kiura/api/valoraciones")
public class ValoracionController {

    private final ValoracionService valoracionService;

    @Autowired
    public ValoracionController(ValoracionService valoracionService) {
        this.valoracionService = valoracionService;
    }

    @GetMapping("/{id}")
    @Secured({ "ADMIN", "NORMAL", "PROFESIONAL" })
    public ResponseEntity<ValoracionResponseDto> obtenerValoracionPorId (@PathVariable Long id) {
        return new ResponseEntity<>(valoracionService.buscarPorId(id), HttpStatus.OK);
    }

    @PostMapping
    @Secured({ "NORMAL" })
    public ResponseEntity<?> registrarValoracion (@RequestBody @Valid ValoracionRequestDto valoracionRequestDto){
        valoracionService.crear(valoracionRequestDto);
        return new ResponseEntity<>(new JsonMessageDto("Nueva valoración registrada",HttpStatus.CREATED.value()), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Secured({ "ADMIN", "USER" })
    public ResponseEntity<?> eliminarValoracionPorId (@PathVariable Long id){
        valoracionService.borrarPorId(id);
        return new ResponseEntity<>(new JsonMessageDto("Valoracion eliminada exitosamente",HttpStatus.OK.value()), HttpStatus.OK);
    }

    @GetMapping
    @Secured("ADMIN")
    public ResponseEntity<List<ValoracionResponseDto>> listarValoraciones (){
        return new ResponseEntity<>(valoracionService.listarTodos(),HttpStatus.OK);
    }

    @PutMapping
    @Secured({ "NORMAL" })
    public ResponseEntity<?> actualizarValoracion (@RequestBody @Valid ValoracionRequestDto valoracionRequestDto){
        valoracionService.actualizar(valoracionRequestDto);
        return new ResponseEntity<>(new JsonMessageDto("Valoracion actualizado exitosamente",HttpStatus.OK.value()), HttpStatus.OK);
    }

}
