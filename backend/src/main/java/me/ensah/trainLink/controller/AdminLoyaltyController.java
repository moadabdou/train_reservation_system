package me.ensah.trainLink.controller;

import me.ensah.trainLink.model.LoyaltyRule;
import me.ensah.trainLink.model.Reward;
import me.ensah.trainLink.services.AdminLoyaltyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/loyalty")
@PreAuthorize("hasRole('ADMIN')")
public class AdminLoyaltyController {

    @Autowired
    private AdminLoyaltyService adminLoyaltyService;

    // --- Rewards ---

    @GetMapping("/rewards")
    public ResponseEntity<List<Reward>> getAllRewards() {
        return ResponseEntity.ok(adminLoyaltyService.getAllRewards());
    }

    @PostMapping("/rewards")
    public ResponseEntity<Reward> createReward(@RequestBody Reward reward) {
        return ResponseEntity.ok(adminLoyaltyService.createReward(reward));
    }

    @PutMapping("/rewards/{id}")
    public ResponseEntity<Reward> updateReward(@PathVariable Long id, @RequestBody Reward reward) {
        return ResponseEntity.ok(adminLoyaltyService.updateReward(id, reward));
    }

    @DeleteMapping("/rewards/{id}")
    public ResponseEntity<Void> deleteReward(@PathVariable Long id) {
        adminLoyaltyService.deleteReward(id);
        return ResponseEntity.noContent().build();
    }

    // --- Rules ---

    @GetMapping("/rules")
    public ResponseEntity<List<LoyaltyRule>> getAllRules() {
        return ResponseEntity.ok(adminLoyaltyService.getAllRules());
    }

    @PutMapping("/rules/{ruleName}")
    public ResponseEntity<LoyaltyRule> updateRule(@PathVariable String ruleName,
            @RequestBody Map<String, Double> payload) {
        Double value = payload.get("value");
        return ResponseEntity.ok(adminLoyaltyService.updateRule(ruleName, value));
    }
}
