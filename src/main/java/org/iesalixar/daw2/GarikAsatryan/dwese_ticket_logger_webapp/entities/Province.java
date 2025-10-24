package org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Province {
    private Long id;
    private String code;
    private String name;
    private Long regionId;
    private String regionName;

    public Province(String code, String name, Long regionId) {
        this.code = code;
        this.name = name;
        this.regionId = regionId;
    }
}
