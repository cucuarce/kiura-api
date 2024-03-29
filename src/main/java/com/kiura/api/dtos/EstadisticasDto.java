package com.kiura.api.dtos;

import lombok.Data;
import org.springframework.stereotype.Component;
@Data
@Component
public class EstadisticasDto {
    private Long totalUsuarios;
    private Long totalContratos;
}
