package me.ensah.trainLink.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seat_number", nullable = false)
    private String number;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id")
    private Train train;

    public Seat(Long id, String number, boolean isAvailable) {
        this.id = id;// even thouth we dont use it we need it for jpa
        this.number = number;
        this.isAvailable = isAvailable;
    }

    // methode for generate seat number
    public static List<Seat> generateSeats(int totalSeats) {
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= totalSeats; i++) {
            String seatNumber = "S" + String.format("%03d", i);
            seats.add(new Seat(null, seatNumber, true));
        }
        return seats;
    }

}