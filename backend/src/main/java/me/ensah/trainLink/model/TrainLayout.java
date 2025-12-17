package me.ensah.trainLink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "train_layouts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainLayout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String layoutName; // e.g., "Al Boraq First Class"

    private int totalRows;
    private int seatsPerRow;

    @Lob
    @Column
    private String layoutConfig; // JSON string representing the seat map (e.g., "[[1,0,1], [1,0,1]]")
}
