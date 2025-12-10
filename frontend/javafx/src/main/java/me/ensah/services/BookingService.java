package me.ensah.services;

import com.fasterxml.jackson.core.type.TypeReference;

import me.ensah.config.Config;
import me.ensah.model.BookingRequest;
import me.ensah.model.BookingResponse;
import me.ensah.model.PaymentRequest;
import me.ensah.model.PaymentResponse;
import me.ensah.net.ApiClient;

import java.io.IOException;

import me.ensah.model.Receipt;

public class BookingService {
    private final ApiClient api;

    public BookingService(ApiClient api) {
        this.api = api;
    }

    public static BookingService defaultInstance() {
        return new BookingService(new ApiClient(Config.apiBaseUrl()));
    }

    public BookingResponse createBooking(BookingRequest request) throws IOException, InterruptedException {
        return api.post("/bookings", request, new TypeReference<BookingResponse>() {
        });
    }

    public PaymentResponse processPayment(PaymentRequest request) throws IOException, InterruptedException {
        return api.post("/payments/process", request, new TypeReference<PaymentResponse>() {
        });
    }

    public Receipt getReceipt(String referenceCode) throws IOException, InterruptedException {
        return api.get("/bookings/" + referenceCode + "/receipt", new TypeReference<Receipt>() {
        });
    }
}
