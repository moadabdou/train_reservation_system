package me.ensah.trainLink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rewards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int costInPoints;

    @Column(nullable = false)
    private String description;

    // Optional: If the reward is a fixed discount percentage or amount
    private Double discountValue;

    // Optional: Type of reward (DISCOUNT, UPGRADE, FREE_TICKET)
    private String type;

}
