package me.ensah.trainLink.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.ensah.trainLink.DTO.BookingResponse;
import me.ensah.trainLink.DTO.BookingSummaryDTO;
import me.ensah.trainLink.DTO.CreateBookingRequest;
import me.ensah.trainLink.DTO.ReceiptDTO;
import me.ensah.trainLink.services.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import me.ensah.trainLink.DTO.PassengerDTO;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getReceipt_ShouldReturnReceiptDTO() throws Exception {
        ReceiptDTO receipt = new ReceiptDTO();
        receipt.setBookingReference("REF123");

        when(bookingService.generateReceipt(anyString())).thenReturn(receipt);

        mockMvc.perform(get("/api/bookings/REF123/receipt")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingReference").value("REF123"));
    }

    @Test
    void createBooking_ShouldReturnBookingResponse() throws Exception {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setScheduleId(1L);
        PassengerDTO passenger = new PassengerDTO();
        // Set passenger details if needed, assuming PassengerDTO has some validation
        // too
        passenger.setName("John Doe");
        passenger.setAge(30);
        request.setPassengers(Collections.singletonList(passenger));

        BookingResponse response = new BookingResponse();
        response.setReferenceCode("REF123");

        when(bookingService.createBooking(any(CreateBookingRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenceCode").value("REF123"));
    }

    @Test
    void listMyBookings_ShouldReturnPageOfBookingSummaryDTO() throws Exception {
        BookingSummaryDTO summary = new BookingSummaryDTO();
        summary.setReferenceCode("REF123");
        Page<BookingSummaryDTO> page = new PageImpl<>(Collections.singletonList(summary));

        when(bookingService.listMyBookings(anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/api/bookings")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].referenceCode").value("REF123"));
    }

    @Test
    void getMyBooking_ShouldReturnBookingResponse() throws Exception {
        BookingResponse response = new BookingResponse();
        response.setReferenceCode("REF123");

        when(bookingService.getMyBookingByRef(anyString())).thenReturn(response);

        mockMvc.perform(get("/api/bookings/REF123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenceCode").value("REF123"));
    }

    @Test
    void cancelMyBooking_ShouldReturnNoContent() throws Exception {
        doNothing().when(bookingService).cancelMyBooking(anyString());

        mockMvc.perform(delete("/api/bookings/REF123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
