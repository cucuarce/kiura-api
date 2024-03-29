package com.kiura.api.controllers;

import com.kiura.api.dtos.EstadisticasDto;
import com.kiura.api.services.EstadisticaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kiura/api/estadisticas")
public class EstadisticaController {
    private final EstadisticaService estadisticasService;

    public EstadisticaController(EstadisticaService estadisticasService) {
        this.estadisticasService = estadisticasService;
    }

    @GetMapping
    @Secured("ADMIN")
    public ResponseEntity<EstadisticasDto> obtenerEstadisticas() {
        EstadisticasDto estadisticas = estadisticasService.obtenerEstadisticas();
        return ResponseEntity.ok(estadisticas);
    }
}
