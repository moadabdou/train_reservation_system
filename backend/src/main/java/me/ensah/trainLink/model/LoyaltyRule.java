package me.ensah.trainLink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "loyalty_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ruleName; // e.g., "POINTS_PER_DOLLAR", "SIGNUP_BONUS"

    @Column(nullable = false)
    private Double value; // e.g., 1.0, 500.0

    private String description;
}
