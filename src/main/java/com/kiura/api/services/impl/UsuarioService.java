package com.kiura.api.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiura.api.dtos.request.UsuarioRequestDto;
import com.kiura.api.dtos.response.UsuarioResponseDto;
import com.kiura.api.entities.Estado;
import com.kiura.api.entities.Rol;
import com.kiura.api.entities.Usuario;
import com.kiura.api.entities.Valoracion;
import com.kiura.api.exceptions.RequestValidationException;
import com.kiura.api.exceptions.ResourceAlreadyExistsException;
import com.kiura.api.exceptions.ResourceNotFoundException;
import com.kiura.api.repositories.IUsuarioRepository;
import com.kiura.api.repositories.IValoracionRepository;
import com.kiura.api.security.auth.AuthService;
import com.kiura.api.services.IService;
import com.kiura.api.utils.MapperClass;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService implements IService<UsuarioResponseDto, UsuarioRequestDto> {
    private final IUsuarioRepository usuarioRepository;

    @Value("${jwt.secretKey}")
    private static final String secretKey = "586E3272357538782F413F4428472B4B6250655368566B597033733676397924";

    private static final ObjectMapper objectMapper = MapperClass.objectMapper();

    private final PasswordEncoder passwordEncoder;
    private final IValoracionRepository valoracionRepository;


    @Autowired
    public UsuarioService(IUsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, IValoracionRepository valoracionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.valoracionRepository = valoracionRepository;
    }


    public void actualizar(UsuarioRequestDto usuarioRequestDto) {
        Usuario usuarioDB = usuarioRepository.findById(usuarioRequestDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no existe", HttpStatus.NOT_FOUND.value()));

        String nuevoNombre = usuarioRequestDto.getNombre();
        if (nuevoNombre != null) {
            usuarioDB.setNombre(nuevoNombre);
        }

        String nuevoApellido = usuarioRequestDto.getApellido();
        if (nuevoApellido != null) {
            usuarioDB.setApellido(nuevoApellido);
        }

        String nuevoMail = usuarioRequestDto.getEmail();
        if (nuevoMail != null) {
            AuthService.validarMail(nuevoMail);
            usuarioDB.setEmail(nuevoMail);
        }

        usuarioDB.setRol(usuarioDB.getRol()); // Mantener el rol
        usuarioDB.setContratosComoUsuarioNormal(usuarioDB.getContratosComoUsuarioNormal()); // Mantener contratos como usuario normal
        usuarioDB.setContratosComoUsuarioProfesional(usuarioDB.getContratosComoUsuarioProfesional()); // Mantener contratos como usuario profesional
        usuarioDB.setPromedioPuntuacion(usuarioDB.getPromedioPuntuacion()); // Mantener el promedio de puntuación
        usuarioDB.setCantidadValoraciones(usuarioDB.getCantidadValoraciones()); // Mantener la cantidad de valoraciones
        usuarioDB.setEstadoAprobacion(usuarioDB.getEstadoAprobacion()); // Mantener el estado de aprobación

        // Validar y actualizar campos específicos según el rol
        switch (usuarioDB.getRol()) {
            case NORMAL:
            case SOPORTE:
                // No se requieren actualizaciones específicas para estos roles
                break;
            case PROFESIONAL:
                // Validar y actualizar campos específicos para el rol de PROFESIONAL
                String nuevoDocumentoIdentidad = usuarioRequestDto.getDocumentoIdentidad();
                if (nuevoDocumentoIdentidad != null) {
                    // Validar el nuevo documento de identidad si es necesario
                    usuarioDB.setDocumentoIdentidad(nuevoDocumentoIdentidad);
                }
                // Mantener la categoría actual
                usuarioDB.setCategoria(usuarioDB.getCategoria());
                break;
            default:
                throw new IllegalArgumentException("Rol no válido");
        }

        usuarioRepository.save(usuarioDB);
    }


    public void cambiarRol(Long usuarioId, Rol nuevoRol) {
        Usuario usuarioDB = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no existe", HttpStatus.NOT_FOUND.value()));

        if (nuevoRol == Rol.ADMIN || nuevoRol == Rol.NORMAL || nuevoRol == Rol.PROFESIONAL || nuevoRol == Rol.SOPORTE) {
            usuarioDB.setRol(nuevoRol);
            usuarioRepository.save(usuarioDB);
        } else {
            throw new RequestValidationException("No tienes permisos para cambiar el rol de este usuario.", HttpStatus.BAD_REQUEST.value());
        }
    }

    @Override
    public UsuarioResponseDto buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("El usuario no existe", HttpStatus.NOT_FOUND.value()));
        return objectMapper.convertValue(usuario, UsuarioResponseDto.class);
    }

    @Override
    public void crear(UsuarioRequestDto usuarioRequestDto) {

        if (usuarioRepository.findByEmail(usuarioRequestDto.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("El usuario ya existe", HttpStatus.CONFLICT.value());
        }

        normalizarNombreApellido(usuarioRequestDto);
        AuthService.validarMail(usuarioRequestDto.getEmail());

        Usuario usuario = objectMapper.convertValue(usuarioRequestDto, Usuario.class);
        String passwordEncriptada = passwordEncoder.encode(usuarioRequestDto.getPassword());
        usuario.setPassword(passwordEncriptada);

        usuarioRepository.save(usuario);
    }

    @Override
    public void borrarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("El usuario no existe", HttpStatus.NOT_FOUND.value()));
        usuarioRepository.delete(usuario);
    }

    @Override
    public List<UsuarioResponseDto> listarTodos() {
        List<Usuario> listaUsuarios = Optional.of(usuarioRepository.findAll()).orElseThrow(() -> new ResourceNotFoundException("No se encontraron usuarios", HttpStatus.NOT_FOUND.value()));

        return listaUsuarios
                .stream()
                .map(usuario -> objectMapper.convertValue(usuario, UsuarioResponseDto.class))
                .collect(Collectors.toList());
    }

    public List<Usuario> buscarProfesionalesPorCategoria(Long categoriaId) {
        return usuarioRepository.findByCategoriaIdAndRol(categoriaId, Rol.PROFESIONAL);
    }

    private void normalizarNombreApellido(UsuarioRequestDto usuarioRequestDto) {

        String inicialNombre = usuarioRequestDto.getNombre().substring(0, 1);
        String restoNombre = usuarioRequestDto.getNombre().substring(1);
        usuarioRequestDto.setNombre(inicialNombre.toUpperCase() + restoNombre.toLowerCase());

        String inicialApellido = usuarioRequestDto.getApellido().substring(0, 1);
        String restoApellido = usuarioRequestDto.getApellido().substring(1);
        usuarioRequestDto.setApellido(inicialApellido.toUpperCase() + restoApellido.toLowerCase());

    }

    public UsuarioResponseDto buscarUsuarioPorToken(String token) {

        // Decodificar el token JWT
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        // Extraer el correo electrónico del usuario desde las reclamaciones del token
        String email = claims.getSubject();

        // Buscar al usuario por correo electrónico en la base de datos
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("El usuario no existe", HttpStatus.NOT_FOUND.value()));

        return objectMapper.convertValue(usuario, UsuarioResponseDto.class);
    }

    public void actualizarPromedioPuntuacion(Usuario usuario) {
        Long usuarioId = usuario.getId();
        List<Valoracion> valoraciones = valoracionRepository.findByUsuarioProfesionalId(usuarioId);

        double sumaPuntuaciones = valoraciones.stream()
                .mapToDouble(Valoracion::getPuntuacion)
                .sum();

        long cantidadValoraciones = valoraciones.size();

        double promedioPuntuacion = cantidadValoraciones > 0 ? sumaPuntuaciones / cantidadValoraciones : 0.0;

        usuario.setCantidadValoraciones(cantidadValoraciones);
        usuario.setPromedioPuntuacion(promedioPuntuacion);
        usuarioRepository.save(usuario);
    }

    public Usuario aprobarProfesional(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no existe", HttpStatus.NOT_FOUND.value()));
        usuario.setEstadoAprobacion(Estado.CONFIRMADO);
        return usuarioRepository.save(usuario);
    }

    public Usuario rechazarProfesional(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no existe", HttpStatus.NOT_FOUND.value()));
        usuario.setEstadoAprobacion(Estado.RECHAZADO);
        return usuarioRepository.save(usuario);
    }
}
