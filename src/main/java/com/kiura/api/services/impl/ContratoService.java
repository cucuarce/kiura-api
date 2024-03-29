package com.kiura.api.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiura.api.dtos.request.ContratoRequestDto;
import com.kiura.api.dtos.response.ContratoResponseDto;
import com.kiura.api.entities.Contrato;
import com.kiura.api.entities.Estado;
import com.kiura.api.entities.Rol;
import com.kiura.api.entities.Usuario;
import com.kiura.api.exceptions.IllegalDateException;
import com.kiura.api.exceptions.ResourceAlreadyExistsException;
import com.kiura.api.exceptions.ResourceNotFoundException;
import com.kiura.api.repositories.IContratoRepository;
import com.kiura.api.repositories.IUsuarioRepository;
import com.kiura.api.services.IService;
import com.kiura.api.utils.MapperClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContratoService implements IService<ContratoResponseDto, ContratoRequestDto> {

    private final IContratoRepository contratoRepository;

    private final IUsuarioRepository usuarioRepository;

    private final UsuarioService usuarioService;

    private static final ObjectMapper objectMapper = MapperClass.objectMapper();

    @Autowired
    public ContratoService(IContratoRepository contratoRepository, IUsuarioRepository usuarioRepository, UsuarioService usuarioService) {
        this.contratoRepository = contratoRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }

    @Override
    public void actualizar(ContratoRequestDto contratoRequestDto) {

        Usuario usuarioNormal = usuarioRepository.findByIdAndRol(
                        contratoRequestDto.getUsuarioNormal().getId(), Rol.NORMAL)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario normal no existe o no tiene el rol adecuado", HttpStatus.NOT_FOUND.value()));

        Usuario usuarioProfesional = usuarioRepository.findByIdAndRol(
                        contratoRequestDto.getUsuarioProfesional().getId(), Rol.PROFESIONAL)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario profesional no existe o no tiene el rol adecuado", HttpStatus.NOT_FOUND.value()));

        Contrato contratoDB = contratoRepository.findById(contratoRequestDto.getId()).orElseThrow(() -> new ResourceNotFoundException("El contrato no existe", HttpStatus.NOT_FOUND.value()));

        if (contratoDB != null) {

            List<Contrato> listaContratoProfesionales = contratoRepository.findByUsuarioProfesionalId(contratoRequestDto.getUsuarioProfesional().getId());

            if(!listaContratoProfesionales.isEmpty()){
                validarSiExisteContratoPrevio(contratoRequestDto);
            }

            validarFechaInicioFinValida(contratoRequestDto.getFechaInicio(), contratoRequestDto.getFechaFin());

            contratoDB.setEstado(Estado.PENDIENTE);

            contratoDB.setUsuarioNormal(usuarioNormal);
            contratoDB.setUsuarioProfesional(usuarioProfesional);
            contratoDB.setFechaInicio(contratoRequestDto.getFechaInicio());
            contratoDB.setFechaFin(contratoRequestDto.getFechaFin());
            contratoDB.setValoracion(false);

            contratoRepository.save(contratoDB);
        }
    }

    @Override
    public ContratoResponseDto buscarPorId(Long id) {
        Contrato contrato = contratoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("El contrato no existe", HttpStatus.NOT_FOUND.value()));
        return objectMapper.convertValue(contrato, ContratoResponseDto.class);

    }

    @Override
    public void crear(ContratoRequestDto contratoRequestDto) {

        validarSiExisteContratoPrevio(contratoRequestDto);
        validarFechaInicioFinValida(contratoRequestDto.getFechaInicio(), contratoRequestDto.getFechaFin());

        Usuario usuarioNormal = usuarioRepository.findByIdAndRol(
                        contratoRequestDto.getUsuarioNormal().getId(), Rol.NORMAL)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario normal no existe o no tiene el rol adecuado", HttpStatus.NOT_FOUND.value()));

        Usuario usuarioProfesional = usuarioRepository.findByIdAndRol(
                        contratoRequestDto.getUsuarioProfesional().getId(), Rol.PROFESIONAL)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario profesional no existe o no tiene el rol adecuado", HttpStatus.NOT_FOUND.value()));

        Contrato contrato = objectMapper.convertValue(contratoRequestDto, Contrato.class);
        contrato.setUsuarioNormal(usuarioNormal);
        contrato.setUsuarioProfesional(usuarioProfesional);
        contrato.setEstado(Estado.PENDIENTE);

        contratoRepository.save(contrato);

    }

    @Override
    public void borrarPorId(Long id) {
        Contrato contrato = contratoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("El contrato no existe", HttpStatus.NOT_FOUND.value()));
        contratoRepository.delete(contrato);
    }

    @Override
    public List<ContratoResponseDto> listarTodos() {
        List<Contrato> contratos = Optional.of(contratoRepository.findAll()).orElseThrow(() -> new ResourceNotFoundException("No se encontraron contratos", HttpStatus.NOT_FOUND.value()));
        return contratos.stream().map(contrato -> objectMapper.convertValue(contrato, ContratoResponseDto.class)).collect(Collectors.toList());

    }

    public List<ContratoResponseDto> obtenerContratosPorUsuario(Long usuarioId) {

        usuarioService.buscarPorId(usuarioId);

        List<Contrato> contratos = Optional.of(contratoRepository.findByUsuarioNormalId(usuarioId)).orElseThrow(() -> new ResourceNotFoundException("No hay contratos registrados para ese usuario", HttpStatus.NOT_FOUND.value()));
        return contratos
                .stream()
                .map(contrato -> objectMapper.convertValue(contrato, ContratoResponseDto.class))
                .collect(Collectors.toList());

    }
    

    private void validarSiExisteContratoPrevio(ContratoRequestDto contratoRequestDto){

        if (contratoRepository.findByUsuarioProfesionalAndFechaInicioAndFechaFin(contratoRequestDto.getUsuarioProfesional().getId(), contratoRequestDto.getFechaInicio(), contratoRequestDto.getFechaFin()).isPresent()) {
            throw new ResourceAlreadyExistsException("Ya existe un contrato en esa fecha para el profesional especificado", HttpStatus.CONFLICT.value());
        }
    }

    private void validarFechaInicioFinValida(LocalDate fechaInicio, LocalDate fechaFin){
        if ( fechaInicio.isBefore(LocalDate.now())) {
            throw new IllegalDateException("La fecha de inicio no puede ser menor a la actual", HttpStatus.BAD_REQUEST.value());
        }

        if ( fechaFin.isBefore(fechaInicio)) {
            throw new IllegalDateException("La fecha de fin no puede ser menor a la fecha de inicio", HttpStatus.BAD_REQUEST.value());
        }
    }


    public List<Contrato> obtenerSolicitudesPendientes(Long idProfesional) {
        return contratoRepository.findSolicitudesPendientesByProfesionalId(idProfesional, Estado.PENDIENTE);
    }

    public void confirmarSolicitudContratacion(Long idContrato) {
        Contrato contrato = contratoRepository.findById(idContrato)
                .orElseThrow(() -> new NotFoundException("Contrato no encontrado con ID: " + idContrato));
        contrato.setEstado(Estado.CONFIRMADO);
        contratoRepository.save(contrato);
    }

    public void rechazarSolicitudContratacion(Long idContrato) {
        Contrato contrato = contratoRepository.findById(idContrato)
                .orElseThrow(() -> new NotFoundException("Contrato no encontrado con ID: " + idContrato));
        contrato.setEstado(Estado.RECHAZADO);
        contratoRepository.save(contrato);
    }
}
