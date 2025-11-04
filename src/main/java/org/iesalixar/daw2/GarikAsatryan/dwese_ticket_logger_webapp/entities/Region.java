package org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Region {
    private Long id;

    @NotEmpty(message = "El código no puede estar vacío")
    @Size(max = 2, message = "El código no puede tener más de 2 caracteres")
    private String code;

    @NotEmpty(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String name;

    public Region(String code, String name) {
        this.code = code;
        this.name = name;
    }
}


