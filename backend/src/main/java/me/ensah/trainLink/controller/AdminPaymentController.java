package me.ensah.trainLink.controller;

import me.ensah.trainLink.DTO.FinancialStatsDTO;
import me.ensah.trainLink.model.Payment;
import me.ensah.trainLink.services.AdminPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminPaymentController {

    @Autowired
    private AdminPaymentService adminPaymentService;

    @GetMapping("/payments")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(adminPaymentService.getAllPayments());
    }

    @PostMapping("/payments/{id}/validate")
    public ResponseEntity<Payment> validatePayment(@PathVariable Long id) {
        return ResponseEntity.ok(adminPaymentService.validatePayment(id));
    }

    @GetMapping("/stats/financial")
    public ResponseEntity<FinancialStatsDTO> getFinancialStats() {
        return ResponseEntity.ok(adminPaymentService.getFinancialStats());
    }
}
