package ma.ensah.services;

import com.fasterxml.jackson.core.type.TypeReference;
import ma.ensah.model.Schedule;
import ma.ensah.net.ApiClient;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ScheduleService {
    private final ApiClient api;

    public ScheduleService(ApiClient api) { this.api = api; }

    public List<Schedule> fetchSchedules(long fromId, long toId, LocalDate date) throws IOException, InterruptedException {
        String path = String.format("/schedules?from=%d&to=%d&date=%s", fromId, toId, date);
        return api.get(path, new TypeReference<List<Schedule>>(){});
    }
}
