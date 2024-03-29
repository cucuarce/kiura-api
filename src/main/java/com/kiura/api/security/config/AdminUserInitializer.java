package com.kiura.api.security.config;

import com.kiura.api.entities.Rol;
import com.kiura.api.entities.Usuario;
import com.kiura.api.repositories.IUsuarioRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AdminUserInitializer {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createInitialAdminUser() {
        if (usuarioRepository.count() == 0) {
            Usuario adminUser = new Usuario();
            adminUser.setNombre("Admin");
            adminUser.setApellido("Admin");
            adminUser.setEmail("admin@example.com");
            String rawPassword = "Admin123";
            String encodedPassword = passwordEncoder.encode(rawPassword);
            adminUser.setPassword(encodedPassword);
            adminUser.setRol(Rol.ADMIN);
            usuarioRepository.save(adminUser);
        }
    }
}
