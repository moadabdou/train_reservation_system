package me.ensah.trainLink.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.ensah.trainLink.DTO.PaymentRequest;
import me.ensah.trainLink.DTO.PaymentResponse;
import me.ensah.trainLink.model.PaymentStatus;
import me.ensah.trainLink.services.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void processPayment_ShouldReturnPaymentResponse() throws Exception {
        PaymentRequest request = new PaymentRequest();
        // Set properties

        PaymentResponse response = new PaymentResponse();
        response.setStatus(PaymentStatus.COMPLETED);

        when(paymentService.processPayment(any(PaymentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void getReceipt_ShouldReturnPaymentResponse() throws Exception {
        PaymentResponse response = new PaymentResponse();
        response.setStatus(PaymentStatus.COMPLETED);

        when(paymentService.getReceipt(anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/payments/1/receipt")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
}
