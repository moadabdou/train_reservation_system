package me.ensah.trainLink.controller;

import me.ensah.trainLink.DTO.LoyaltyStatusDTO;
import me.ensah.trainLink.model.Reward;
import me.ensah.trainLink.services.LoyaltyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loyalty")
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    public LoyaltyController(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    @GetMapping("/status")
    public ResponseEntity<LoyaltyStatusDTO> getStatus() {
        return ResponseEntity.ok(loyaltyService.getMyStatus());
    }

    @GetMapping("/rewards")
    public ResponseEntity<List<Reward>> getRewards() {
        return ResponseEntity.ok(loyaltyService.getAllRewards());
    }

    @PostMapping("/redeem/{rewardId}")
    public ResponseEntity<Map<String, String>> redeemReward(@PathVariable Long rewardId) {
        String voucherCode = loyaltyService.redeemReward(rewardId);
        return ResponseEntity.ok(Map.of("voucherCode", voucherCode));
    }
}
