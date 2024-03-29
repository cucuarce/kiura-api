package com.kiura.api.security.auth;

import com.kiura.api.entities.*;
import com.kiura.api.exceptions.RequestValidationException;
import com.kiura.api.exceptions.ResourceAlreadyExistsException;
import com.kiura.api.repositories.ICategoriaRepository;
import com.kiura.api.repositories.IUsuarioRepository;
import com.kiura.api.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final IUsuarioRepository usuarioRepository;

    private final ICategoriaRepository categoriaRepository;

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDto login(LoginRequestDto request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        UserDetails usuario = usuarioRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtService.getToken(usuario);
        return AuthResponseDto.builder()
                .token(token)
                .build();
    }

    public AuthResponseDto register(RegisterRequestDto request) {

        validarMail(request.getEmail());
        validarPassword(request.getPassword());
        normalizarNombreApellido(request);
        validarDatosComunes(request);

        Usuario usuario;

        switch (request.getRol()) {
            case NORMAL:
            case SOPORTE:
                usuario = crearUsuarioNormalOSoporte(request);
                break;
            case PROFESIONAL:
                validarDatosProfesional(request);
                usuario = crearUsuarioProfesional(request);
                usuario.setEstadoAprobacion(Estado.PENDIENTE);
                usuarioRepository.save(usuario);
                return AuthResponseDto.builder().message("Usuario registrado exitosamente, pendiente de aprobación").build();
            default:
                throw new IllegalArgumentException("Rol no válido");
        }

        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("El usuario ya existe", HttpStatus.CONFLICT.value());
        }

        usuarioRepository.save(usuario);
        return AuthResponseDto.builder().build();
    }

    private Usuario crearUsuarioNormalOSoporte(RegisterRequestDto request) {
        return new Usuario(
                request.getNombre(),
                request.getApellido(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getRol()
        );
    }

    private Usuario crearUsuarioProfesional(RegisterRequestDto request) {
        Usuario profesional = crearUsuarioNormalOSoporte(request);
        profesional.setFoto(request.getFoto());
        profesional.setDocumentoIdentidad(request.getDocumentoIdentidad());
        profesional.setCertificadosEstudios(request.getCertificadosEstudios());
        //profesional.setCategoria(request.getCategoria());
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        profesional.setCategoria(categoria);
        return profesional;
    }

    private void validarDatosComunes(RegisterRequestDto request) {
        if (request.getNombre() == null || request.getNombre().isEmpty()) {
            throw new RequestValidationException("El nombre es obligatorio", HttpStatus.BAD_REQUEST.value());
        } else if (request.getNombre().length() < 3 || request.getNombre().length() > 15) {
            throw new RequestValidationException("El nombre debe tener entre 3 y 15 caracteres", HttpStatus.BAD_REQUEST.value());
        }

        if (request.getApellido() == null || request.getApellido().isEmpty()) {
            throw new RequestValidationException("El apellido es obligatorio", HttpStatus.BAD_REQUEST.value());
        } else if (request.getApellido().length() < 3 || request.getApellido().length() > 15) {
            throw new RequestValidationException("El apellido debe tener entre 3 y 15 caracteres", HttpStatus.BAD_REQUEST.value());
        }

    }

    private void validarDatosProfesional(RegisterRequestDto request) {

        if (request.getFoto() == null || request.getFoto().isEmpty()) {
            throw new RequestValidationException("La foto es obligatoria para un profesional", HttpStatus.BAD_REQUEST.value());
        }

        if (request.getDocumentoIdentidad() == null || request.getDocumentoIdentidad().isEmpty()) {
            throw new RequestValidationException("El documento de identidad es obligatorio para un profesional", HttpStatus.BAD_REQUEST.value());
        }

        if (request.getCategoriaId() == null) {
            throw new RequestValidationException("La categoría es obligatoria para un profesional", HttpStatus.BAD_REQUEST.value());
        }

        if (request.getCertificadosEstudios() == null || request.getCertificadosEstudios().isEmpty()) {
            throw new RequestValidationException("Se requiere al menos un certificado de estudio para un profesional", HttpStatus.BAD_REQUEST.value());
        }

    }

    private void normalizarNombreApellido(RegisterRequestDto request) {

        String inicialNombre = request.getNombre().substring(0, 1);
        String restoNombre = request.getNombre().substring(1);
        request.setNombre(inicialNombre.toUpperCase() + restoNombre.toLowerCase());

        String inicialApellido = request.getApellido().substring(0, 1);
        String restoApellido = request.getApellido().substring(1);
        request.setApellido(inicialApellido.toUpperCase() + restoApellido.toLowerCase());

    }

    public static boolean validarMail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        if (matcher.matches()) {
            return true;
        } else {
            throw new RequestValidationException("El email no cumple con los valores especificados", HttpStatus.BAD_REQUEST.value());
        }
    }


    public static Boolean validarPassword(String password) {
        String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)[A-Za-z\\d]{8,12}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);

        if (matcher.matches()) {
            return true;
        } else {
            throw new RequestValidationException("La contraseña no cumple con los valores especificados", HttpStatus.BAD_REQUEST.value());
        }
    }
}
