package me.ensah.trainLink.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoyaltyStatusDTO {
    private int pointsBalance;
    private String tierLevel;
    private int pointsToNextTier;
    private String nextTier;
}
