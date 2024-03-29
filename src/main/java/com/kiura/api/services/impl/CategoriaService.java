package com.kiura.api.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiura.api.dtos.request.CategoriaRequestDto;
import com.kiura.api.dtos.response.CategoriaResponseDto;
import com.kiura.api.entities.Categoria;
import com.kiura.api.entities.Usuario;
import com.kiura.api.exceptions.ResourceAlreadyExistsException;
import com.kiura.api.exceptions.ResourceNotFoundException;
import com.kiura.api.repositories.ICategoriaRepository;
import com.kiura.api.repositories.IUsuarioRepository;
import com.kiura.api.services.IService;
import com.kiura.api.utils.MapperClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoriaService implements IService<CategoriaResponseDto, CategoriaRequestDto> {

    private final ICategoriaRepository categoriaRepository;

    private final IUsuarioRepository usuarioRepository;

    private static final ObjectMapper objectMapper = MapperClass.objectMapper();

    @Autowired
    public CategoriaService(ICategoriaRepository categoriaRepository, IUsuarioRepository usuarioRepository) {
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void actualizar(CategoriaRequestDto categoriaRequestDto){
        Categoria categoriaDB = categoriaRepository.findById(categoriaRequestDto.getId()).orElseThrow(() -> new ResourceNotFoundException("La categoria no existe", HttpStatus.NOT_FOUND.value()));

        if (categoriaDB != null) {

            normalizarNombreDescripcion(categoriaRequestDto);
            categoriaDB = objectMapper.convertValue(categoriaRequestDto, Categoria.class);

            categoriaRepository.save(categoriaDB);
        }
    }

    @Override
    public CategoriaResponseDto buscarPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("La categoria especificada no existe", HttpStatus.NOT_FOUND.value()));
        return objectMapper.convertValue(categoria, CategoriaResponseDto.class);
    }

    @Override
    public void crear(CategoriaRequestDto categoriaRequestDto){
        normalizarNombreDescripcion(categoriaRequestDto);

        if (categoriaRepository.findByNombre(categoriaRequestDto.getNombre()).isPresent()) {
            throw new ResourceAlreadyExistsException("La categoria ya existe", HttpStatus.CONFLICT.value());
        }

        Categoria categoria = objectMapper.convertValue(categoriaRequestDto, Categoria.class);
        categoriaRepository.save(categoria);
    }

    @Override
    public void borrarPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("La categoria ha sido borrada", HttpStatus.NOT_FOUND.value()));

        List<Usuario> usuarios = usuarioRepository.findByCategoria(categoria);

        // Actualizar la referencia a la categoría en cada usuario
        for (Usuario usuario : usuarios) {
            usuario.setCategoria(null);
            usuarioRepository.save(usuario);
        }

        // Guardar los cambios en los usuarios antes de eliminar la categoría
        usuarioRepository.flush();

        categoriaRepository.delete(categoria);
    }

    @Override
    public List<CategoriaResponseDto> listarTodos() {
        List<Categoria> categorias = Optional.of(categoriaRepository.findAll()).orElseThrow(() -> new ResourceNotFoundException("No se encontro ninguna categoria", HttpStatus.NOT_FOUND.value()));
        return categorias.stream().map(categoria -> objectMapper.convertValue(categoria, CategoriaResponseDto.class)).collect(Collectors.toList());
    }

    private void normalizarNombreDescripcion(CategoriaRequestDto categoriaRequestDto) {

        String inicialNombre = categoriaRequestDto.getNombre().substring(0, 1);
        String restoNombre = categoriaRequestDto.getNombre().substring(1);
        categoriaRequestDto.setNombre(inicialNombre.toUpperCase() + restoNombre.toLowerCase());

        String inicialDescripcion = categoriaRequestDto.getDescripcion().substring(0, 1);
        String restoDescripcion = categoriaRequestDto.getDescripcion().substring(1);
        categoriaRequestDto.setDescripcion(inicialDescripcion.toUpperCase() + restoDescripcion.toLowerCase());
    }

}
