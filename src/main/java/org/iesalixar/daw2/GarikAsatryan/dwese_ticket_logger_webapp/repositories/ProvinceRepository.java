package org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.repositories;

import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities.Province;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProvinceRepository extends JpaRepository<Province, Long> {
    List<Province> listAllProvinces();

    void insertProvince(Province province);

    void updateProvince(Province province);

    void deleteProvince(Long id);

    Province getProvinceById(Long id);

    boolean existsProvinceByCode(String code);

    boolean existsProvinceByCodeAndNotId(String code, Long id);
}
