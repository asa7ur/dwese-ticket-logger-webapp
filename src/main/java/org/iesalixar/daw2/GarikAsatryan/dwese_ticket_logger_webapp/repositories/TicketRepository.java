package org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.repositories;

import java.util.List;

import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByDiscountGreaterThan(Float discount);
}
