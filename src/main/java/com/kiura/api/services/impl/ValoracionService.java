package com.kiura.api.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiura.api.dtos.request.ContratoRequestDto;
import com.kiura.api.dtos.request.UsuarioRequestDto;
import com.kiura.api.dtos.request.ValoracionRequestDto;
import com.kiura.api.dtos.response.ValoracionResponseDto;
import com.kiura.api.entities.Contrato;
import com.kiura.api.entities.Rol;
import com.kiura.api.entities.Usuario;
import com.kiura.api.entities.Valoracion;
import com.kiura.api.exceptions.ResourceAlreadyExistsException;
import com.kiura.api.exceptions.ResourceNotFoundException;
import com.kiura.api.repositories.IContratoRepository;
import com.kiura.api.repositories.IUsuarioRepository;
import com.kiura.api.repositories.IValoracionRepository;
import com.kiura.api.services.IService;
import com.kiura.api.utils.MapperClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ValoracionService implements IService<ValoracionResponseDto, ValoracionRequestDto> {

    private final IValoracionRepository valoracionRepository;

    private final IContratoRepository contratoRepository;
    private final IUsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private static final ObjectMapper objectMapper = MapperClass.objectMapper();

    @Autowired
    public ValoracionService(IValoracionRepository valoracionRepository, IContratoRepository contratoRepository, IUsuarioRepository usuarioRepository, UsuarioService usuarioService) {
        this.valoracionRepository = valoracionRepository;
        this.contratoRepository = contratoRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }

    public List<ValoracionResponseDto> obtenerValoracionesPorProfesoinal(Long profesionalId) {

        usuarioService.buscarPorId(profesionalId);

        List<Valoracion> valoraciones = Optional.of(valoracionRepository.findByUsuarioProfesionalId(profesionalId)).orElseThrow(() -> new ResourceNotFoundException("No hay valoraciones registradas para este profesional", HttpStatus.NOT_FOUND.value()));
        return valoraciones
                .stream()
                .map(valoracion -> objectMapper.convertValue(valoracion, ValoracionResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public void actualizar(ValoracionRequestDto valoracionRequestDto) {
        Valoracion valoracion = valoracionRepository.findById(valoracionRequestDto.getId()).orElseThrow(() -> new ResourceNotFoundException("La valoración solicitada no existe", HttpStatus.NOT_FOUND.value()));
        if (valoracion != null) {
            valoracion = objectMapper.convertValue(valoracionRequestDto, Valoracion.class);
            valoracionRepository.save(valoracion);
            usuarioService.actualizarPromedioPuntuacion(valoracion.getUsuarioProfesional());
        }
    }

    @Transactional
    @Override
    public ValoracionResponseDto buscarPorId(Long id) {
        Valoracion valoracion = valoracionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("La valoración solicitada no existe", HttpStatus.NOT_FOUND.value()));
        return objectMapper.convertValue(valoracion, ValoracionResponseDto.class);
    }

    @Override
    public void crear(ValoracionRequestDto valoracionRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuario = authentication.getName();

        Usuario usuarioNormal = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo electrónico: " + emailUsuario));

        ContratoRequestDto contratoRequestDto = valoracionRequestDto.getContrato();
        if (contratoRequestDto == null || contratoRequestDto.getId() == null) {
            throw new ResourceNotFoundException("La solicitud no contiene información de contrato válido. No se puede crear la valoración.", HttpStatus.NOT_FOUND.value());
        }

        Contrato contrato = contratoRepository.findById(contratoRequestDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Contrato no encontrado con ID: " + contratoRequestDto.getId(), HttpStatus.NOT_FOUND.value()));

        if (valoracionRepository.existsByUsuarioNormalAndContrato(usuarioNormal, contrato)) {
            throw new ResourceAlreadyExistsException("Ya existe una valoración para este contrato y usuario.", HttpStatus.CONFLICT.value());
        }

        UsuarioRequestDto usuarioProfesionalDto = valoracionRequestDto.getUsuarioProfesional();
        if (usuarioProfesionalDto == null || usuarioProfesionalDto.getId() == null) {
            throw new ResourceNotFoundException("No se especificó el usuario profesional en la solicitud.", HttpStatus.NOT_FOUND.value());
        }

        Usuario usuarioProfesional = usuarioRepository.findByIdAndRol(
                        usuarioProfesionalDto.getId(), Rol.PROFESIONAL)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario profesional no existe o no tiene el rol adecuado", HttpStatus.NOT_FOUND.value()));


        Valoracion valoracion = objectMapper.convertValue(valoracionRequestDto, Valoracion.class);
        valoracion.setUsuarioNormal(usuarioNormal);
        valoracion.setUsuarioProfesional(usuarioProfesional);
        valoracion.setContrato(contrato);
        valoracion.getContrato().setValoracion(true);

        valoracionRepository.save(valoracion);
        usuarioService.actualizarPromedioPuntuacion(usuarioProfesional);
    }

    @Override
    public void borrarPorId(Long id) {
        Valoracion valoracion = valoracionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("La valoracion solicitada no existe", HttpStatus.NOT_FOUND.value()));
        valoracionRepository.delete(valoracion);
        usuarioService.actualizarPromedioPuntuacion(valoracion.getUsuarioProfesional());
    }

    @Override
    public List<ValoracionResponseDto> listarTodos() {
        List<Valoracion> listaValoraciones = Optional.of(valoracionRepository.findAll()).orElseThrow(() -> new ResourceNotFoundException("No se encontraron valoraciones", HttpStatus.NOT_FOUND.value()));

        return listaValoraciones
                .stream()
                .map(valoracion -> objectMapper.convertValue(valoracion, ValoracionResponseDto.class))
                .collect(Collectors.toList());
    }

}
