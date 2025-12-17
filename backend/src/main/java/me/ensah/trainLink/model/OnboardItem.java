package me.ensah.trainLink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "onboard_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OnboardItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String category; // e.g., "FOOD", "DRINK", "SNACK"

    @Column(nullable = false)
    private boolean isAvailable = true;
}
