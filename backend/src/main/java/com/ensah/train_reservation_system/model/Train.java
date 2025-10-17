package com.ensah.train_reservation_system.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trains")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "total_seats", nullable = false)
    private int totalSeats;

}
