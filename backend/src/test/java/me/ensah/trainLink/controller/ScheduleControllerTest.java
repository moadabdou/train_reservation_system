package me.ensah.trainLink.controller;

import me.ensah.trainLink.DTO.RouteStopDTO;
import me.ensah.trainLink.DTO.ScheduleDTO;
import me.ensah.trainLink.DTO.TrainPositionDTO;
import me.ensah.trainLink.services.ScheduleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScheduleController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScheduleService scheduleService;

    @Test
    void getRoute_ShouldReturnListOfRouteStopDTO() throws Exception {
        RouteStopDTO stop = new RouteStopDTO();
        // Set properties if needed
        List<RouteStopDTO> route = Collections.singletonList(stop);

        when(scheduleService.getRouteStops(anyLong())).thenReturn(route);

        mockMvc.perform(get("/api/schedules/1/route")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void searchSchedules_ShouldReturnListOfScheduleDTO() throws Exception {
        ScheduleDTO schedule = new ScheduleDTO();
        // Set properties
        List<ScheduleDTO> schedules = Collections.singletonList(schedule);

        when(scheduleService.findSchedules(anyLong(), anyLong(), any(LocalDate.class))).thenReturn(schedules);

        mockMvc.perform(get("/api/schedules")
                .param("from", "1")
                .param("to", "2")
                .param("date", LocalDate.now().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getPosition_ShouldReturnTrainPositionDTO() throws Exception {
        TrainPositionDTO position = new TrainPositionDTO();
        // Set properties

        when(scheduleService.getTrainPosition(anyLong())).thenReturn(position);

        mockMvc.perform(get("/api/schedules/1/position")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
