package org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.dao;

import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities.Region;
import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities.dto.RegionDTO;

import java.util.List;

public interface RegionDAO {
    RegionDTO listAllRegions(int currentPage);

    List<Region> listAllRegions();

    void insertRegion(Region region);

    void updateRegion(Region region);

    void deleteRegion(Long id);

    Region getRegionById(Long id);

    boolean existsRegionByCode(String code);

    boolean existsRegionByCodeAndNotId(String code, Long id);
}