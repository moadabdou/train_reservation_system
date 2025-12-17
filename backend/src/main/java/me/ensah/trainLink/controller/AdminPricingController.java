package me.ensah.trainLink.controller;

import me.ensah.trainLink.model.PricingRule;
import me.ensah.trainLink.model.TrainLayout;
import me.ensah.trainLink.services.AdminPricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/pricing")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPricingController {

    @Autowired
    private AdminPricingService adminPricingService;

    // --- Layouts ---

    @GetMapping("/layouts")
    public ResponseEntity<List<TrainLayout>> getAllLayouts() {
        return ResponseEntity.ok(adminPricingService.getAllLayouts());
    }

    @PostMapping("/layouts")
    public ResponseEntity<TrainLayout> createLayout(@RequestBody TrainLayout layout) {
        return ResponseEntity.ok(adminPricingService.saveLayout(layout));
    }

    @DeleteMapping("/layouts/{id}")
    public ResponseEntity<Void> deleteLayout(@PathVariable Long id) {
        adminPricingService.deleteLayout(id);
        return ResponseEntity.noContent().build();
    }

    // --- Rules ---

    @GetMapping("/rules")
    public ResponseEntity<List<PricingRule>> getAllRules() {
        return ResponseEntity.ok(adminPricingService.getAllRules());
    }

    @PostMapping("/rules")
    public ResponseEntity<PricingRule> createRule(@RequestBody PricingRule rule) {
        return ResponseEntity.ok(adminPricingService.saveRule(rule));
    }

    @DeleteMapping("/rules/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        adminPricingService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}
