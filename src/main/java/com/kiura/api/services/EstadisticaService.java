package com.kiura.api.services;

import com.kiura.api.dtos.EstadisticasDto;
import com.kiura.api.repositories.IContratoRepository;
import com.kiura.api.repositories.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstadisticaService {
    private final IUsuarioRepository usuarioRepository;
    private final IContratoRepository contratoRepository;

    public EstadisticaService(IUsuarioRepository usuarioRepository, IContratoRepository contratoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.contratoRepository = contratoRepository;
    }

    public EstadisticasDto obtenerEstadisticas() {
        EstadisticasDto estadisticas = new EstadisticasDto();
        estadisticas.setTotalUsuarios(usuarioRepository.count());
        estadisticas.setTotalContratos(contratoRepository.count());

        return estadisticas;
    }
}
