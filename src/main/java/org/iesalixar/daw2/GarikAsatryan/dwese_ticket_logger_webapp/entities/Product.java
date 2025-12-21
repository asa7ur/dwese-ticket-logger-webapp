package org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"tickets"})
@EqualsAndHashCode(exclude = {"tickets"})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "{msg.product.name.notEmpty}")
    @Size(min = 2, max = 100, message = "{msg.product.name.size}")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "{msg.product.price.notNull}")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToMany(mappedBy = "products")
    private List<Ticket> tickets;
}


