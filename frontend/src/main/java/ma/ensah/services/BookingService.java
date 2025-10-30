package ma.ensah.services;

import com.fasterxml.jackson.core.type.TypeReference;
import ma.ensah.config.Config;
import ma.ensah.model.BookingRequest;
import ma.ensah.model.BookingResponse;
import ma.ensah.net.ApiClient;

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
