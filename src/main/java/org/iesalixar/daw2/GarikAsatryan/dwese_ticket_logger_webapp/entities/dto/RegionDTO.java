package org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities.Region;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegionDTO {
    private List<Region> regions;
    private int pages;
    private int currentPage;
}