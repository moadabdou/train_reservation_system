package me.ensah.trainLink.controller;

import me.ensah.trainLink.DTO.PaymentRequest;
import me.ensah.trainLink.DTO.PaymentResponse;
import me.ensah.trainLink.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payments/process")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/payments/{id}/receipt")
    public ResponseEntity<PaymentResponse> getReceipt(@PathVariable Long id) {
        PaymentResponse response = paymentService.getReceipt(id);
        return ResponseEntity.ok(response);
    }
}
