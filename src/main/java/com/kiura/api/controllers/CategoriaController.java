package com.kiura.api.controllers;

import com.kiura.api.dtos.JsonMessageDto;
import com.kiura.api.dtos.request.CategoriaRequestDto;
import com.kiura.api.dtos.response.CategoriaResponseDto;
import com.kiura.api.services.IService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/kiura")
public class CategoriaController {

    private final IService<CategoriaResponseDto, CategoriaRequestDto> categoriaService;

    @Autowired
    public CategoriaController(IService<CategoriaResponseDto, CategoriaRequestDto> categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping("/categorias/{id}")
    public ResponseEntity<CategoriaResponseDto> obtenerCategoriaPorId (@PathVariable Long id) {
        return new ResponseEntity<>(categoriaService.buscarPorId(id), HttpStatus.OK);
    }

    @PostMapping("/api/categorias")
    @Secured("ADMIN")
    public ResponseEntity<?> registrarCategoria (@RequestBody @Valid CategoriaRequestDto categoriaRequestDto){
        categoriaService.crear(categoriaRequestDto);
        return new ResponseEntity<>(new JsonMessageDto("Nueva categoría registrada.",HttpStatus.CREATED.value()) , HttpStatus.CREATED);
    }

    @DeleteMapping("/api/categorias/{id}")
    @Secured("ADMIN")
    public ResponseEntity<?> eliminarCategoriaPorId (@PathVariable Long id){
        categoriaService.borrarPorId(id);
        return new ResponseEntity<>(new JsonMessageDto("Categoría eliminada.",HttpStatus.OK.value()), HttpStatus.OK);
    }

    @GetMapping("/categorias")
    @Secured({"ADMIN", "NORMAL", "PROFESIONAL"})
    public ResponseEntity<List<CategoriaResponseDto>> listaDeCategorias(){
        return new ResponseEntity<>(categoriaService.listarTodos(), HttpStatus.OK);
    }

    @PutMapping("/api/categorias")
    @Secured("ADMIN")
    public ResponseEntity<?> actualizarCategoria (@RequestBody @Valid CategoriaRequestDto categoriaRequestDto){
        categoriaService.actualizar(categoriaRequestDto);
        return new ResponseEntity<>(new JsonMessageDto("Categoría actualizada.",HttpStatus.OK.value()), HttpStatus.OK);
    }
}
