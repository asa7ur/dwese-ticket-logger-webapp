package org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "supermarkets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Supermarket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "{msg.supermarket.name.notEmpty}")
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "supermarket", cascade = CascadeType.ALL, fetch =
            FetchType.LAZY)
    private List<Location> locations;

    public Supermarket(String name) {
        this.name = name;
    }
}
