package org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.repositories;

import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProvinceRepository extends JpaRepository<Province, Long> {
    //    List<Province> findAll();
//
//    void deleteById(Long id);
//
//    Optional<Province> findById(Long id);
//
    boolean existsProvinceByCode(String code);

    @Query("SELECT COUNT(r) > 0 FROM Region r WHERE r.code = :code AND r.id != :id")
    boolean existsProvinceByCodeAndNotId(@Param("code") String code, @Param("id") Long id);
}
