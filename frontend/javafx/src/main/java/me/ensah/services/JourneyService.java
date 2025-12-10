package me.ensah.services;

import com.fasterxml.jackson.core.type.TypeReference;
import me.ensah.model.RouteStop;
import me.ensah.model.TrainPosition;
import me.ensah.net.ApiClient;

import java.io.IOException;
import java.util.List;

public class JourneyService {
    private final ApiClient api;

    public JourneyService(ApiClient api) {
        this.api = api;
    }

    public List<RouteStop> getRoute(Long scheduleId) throws IOException, InterruptedException {
        String path = String.format("/schedules/%d/route", scheduleId);
        return api.get(path, new TypeReference<List<RouteStop>>() {
        });
    }

    public TrainPosition getPosition(Long scheduleId) throws IOException, InterruptedException {
        String path = String.format("/schedules/%d/position", scheduleId);
        return api.get(path, new TypeReference<TrainPosition>() {
        });
    }
}
