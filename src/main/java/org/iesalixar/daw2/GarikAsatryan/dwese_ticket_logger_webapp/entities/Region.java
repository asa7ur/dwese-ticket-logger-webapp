package org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Region {
    private Long id;
    private String code;
    private String name;

    public Region(String code, String name) {
        this.code = code;
        this.name = name;
    }
}


