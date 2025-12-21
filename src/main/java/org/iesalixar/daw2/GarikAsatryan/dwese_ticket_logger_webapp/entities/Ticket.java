package org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"products"})
@EqualsAndHashCode(exclude = {"products"})
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "{msg.ticket.date.notNull}")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "date", nullable = false)
    private Date date;

    @NotNull(message = "{msg.ticket.discount.notNull}")
    @Column(name = "discount", nullable = false, precision = 5, scale = 2)
    private BigDecimal discount;

    @ManyToMany
    @JoinTable(
            name = "product_ticket",
            joinColumns = @JoinColumn(name = "ticket_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;

    @Transient
    public BigDecimal getTotal() {
        if (products == null || products.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;
        for (Product product : products) {
            total = total.add(product.getPrice());
        }

        if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discountPercentage = discount.divide(BigDecimal.valueOf(100));
            total = total.subtract(total.multiply(discountPercentage));
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }
}
