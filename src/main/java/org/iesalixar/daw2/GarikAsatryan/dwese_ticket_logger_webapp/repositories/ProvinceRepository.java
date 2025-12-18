package org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.repositories;

import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities.Province;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProvinceRepository extends JpaRepository<Province, Long> {
    boolean existsProvinceByCode(String code);

    @Query("SELECT COUNT(p) > 0 FROM Province p WHERE p.code = :code AND p.id != :id")
    boolean existsProvinceByCodeAndNotId(@Param("code") String code, @Param("id") Long id);

    @Query("SELECT p FROM Province p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :term, '%')) OR LOWER(p.region.name) LIKE LOWER(CONCAT('%', :term, '%'))")
    Page<Province> searchProvinces(@Param("term") String term, Pageable pageable);
}