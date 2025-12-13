package me.ensah.trainLink.controller;

import me.ensah.trainLink.DTO.StationDTO;
import me.ensah.trainLink.services.StationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class StationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StationService stationService;

    @Test
    void getStations_ShouldReturnListOfStations() throws Exception {
        StationDTO station1 = new StationDTO(1L, "Station A", 0.0, 0.0, "City A", null, null);
        StationDTO station2 = new StationDTO(2L, "Station B", 0.0, 0.0, "City B", null, null);
        List<StationDTO> stations = Arrays.asList(station1, station2);

        when(stationService.getAllStations()).thenReturn(stations);

        mockMvc.perform(get("/api/stations")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Station A"));
    }

    @Test
    void getStationInfo_ShouldReturnStationDTO() throws Exception {
        StationDTO station = new StationDTO(1L, "Station A", 0.0, 0.0, "City A", null, null);

        when(stationService.getStationInfo(anyLong())).thenReturn(station);

        mockMvc.perform(get("/api/stations/1/info")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Station A"));
    }
}
