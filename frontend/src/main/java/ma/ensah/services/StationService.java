package ma.ensah.services;

import com.fasterxml.jackson.core.type.TypeReference;
import ma.ensah.model.Station;
import ma.ensah.net.ApiClient;

import java.io.IOException;
import java.util.List;

public class StationService {
    private final ApiClient api;

    public StationService(ApiClient api) { this.api = api; }

    public List<Station> fetchStations() throws IOException, InterruptedException {
        List<Station> list = api.get("/stations", new TypeReference<List<Station>>(){});
        list.sort((a,b) -> a.getName().compareToIgnoreCase(b.getName()));
        return list;
    }
}
