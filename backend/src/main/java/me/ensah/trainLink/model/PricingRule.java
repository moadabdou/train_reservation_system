package me.ensah.trainLink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pricing_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PricingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ruleName; // e.g., "Weekend Surcharge"

    @Column(nullable = false)
    private String conditionType; // e.g., "WEEKEND", "BOOKING_DAYS_BEFORE", "PEAK_HOUR"

    private String conditionValue; // e.g., "2" (days), "18:00-20:00"

    @Column(nullable = false)
    private Double multiplier; // e.g., 1.10 for +10%, 0.90 for -10%

    private boolean isActive = true;
}
