package me.ensah.trainLink.model;

import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider provider;

    // list of seats in the train
    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats;
    // lets understand that a train has many seats so to represent that we use
    // OneToMany relationship
    // we use mappedBy to specify the field in the Seat entity that owns the
    // relationship
    // here it is "train" field in Seat class
    // cascade = CascadeType.ALL means that any operation performed on Train will be
    // cascaded to its seats
    // orphanRemoval = true means that if a seat is removed from the train's seat
    // list, it will be deleted from the database as well

}
