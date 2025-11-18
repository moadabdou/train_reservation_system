package me.ensah.services;

import com.fasterxml.jackson.core.type.TypeReference;

import me.ensah.config.Config;
import me.ensah.model.BookingRequest;
import me.ensah.model.BookingResponse;
import me.ensah.net.ApiClient;

import java.io.IOException;

public class BookingService {
    private final ApiClient api;

    public BookingService(ApiClient api) {
        this.api = api;
    }

    public static BookingService defaultInstance() {
        return new BookingService(new ApiClient(Config.apiBaseUrl()));
    }

    public BookingResponse createBooking(BookingRequest request) throws IOException, InterruptedException {
        return api.post("/bookings", request, new TypeReference<BookingResponse>(){});
    }
}
