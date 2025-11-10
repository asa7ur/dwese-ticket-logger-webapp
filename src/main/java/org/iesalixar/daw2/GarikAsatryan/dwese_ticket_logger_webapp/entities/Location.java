package org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "{msg.location.address.notEmpty}")
    @Column(name = "address", nullable = false)
    private String address;

    @NotEmpty(message = "{msg.location.city.notEmpty}")
    @Column(name = "city", nullable = false)
    private String city;

    @NotNull(message = "{msg.location.supermarket.notNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supermarket_id", nullable = false)
    private Supermarket supermarket;

    @NotNull(message = "{msg.location.province.notNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id", nullable = false)
    private Province province;

    public Location(String address, String city, Supermarket supermarket,
                    Province province) {
        this.address = address;
        this.city = city;
        this.supermarket = supermarket;
        this.province = province;
    }
}
