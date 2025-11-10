package org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.dao;

import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities.Province;

import java.sql.SQLException;
import java.util.List;

public interface ProvinceDAO {
    List<Province> listAllProvinces();

    void insertProvince(Province province);

    void updateProvince(Province province);

    void deleteProvince(Long id);

    Province getProvinceById(Long id);

    boolean existsProvinceByCode(String code);

    boolean existsProvinceByCodeAndNotId(String code, Long id);
}
