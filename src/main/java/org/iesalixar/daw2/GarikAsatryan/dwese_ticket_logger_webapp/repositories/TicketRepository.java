package org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.repositories;

import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByDiscountGreaterThan(BigDecimal discount);
}
