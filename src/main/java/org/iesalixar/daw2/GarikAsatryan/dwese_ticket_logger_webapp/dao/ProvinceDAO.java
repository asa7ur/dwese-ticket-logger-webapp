package org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.dao;

import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities.Province;

import java.sql.SQLException;
import java.util.List;

public interface ProvinceDAO {
    List<Province> listAllProvinces() throws SQLException;

    void insertProvince(Province province) throws SQLException;

    void updateProvince(Province province) throws SQLException;

    void deleteProvince(long id) throws SQLException;

    Province getProvinceById(long id) throws SQLException;

    boolean existsProvinceByCode(String code) throws SQLException;

    boolean existsProvinceByCodeAndNotId(String code, long id) throws SQLException;
}
