package me.ensah.trainLink.services;

import me.ensah.trainLink.DTO.TrainDTO;
import me.ensah.trainLink.model.Provider;
import me.ensah.trainLink.model.Seat;
import me.ensah.trainLink.model.Train;
import me.ensah.trainLink.model.TrainLayout;
import me.ensah.trainLink.repository.ProviderRepository;
import me.ensah.trainLink.repository.TrainRepository;
import me.ensah.trainLink.repository.TrainLayoutRepository;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainService {

    private final TrainRepository trainRepository;
    private final ProviderRepository providerRepository;
    private final TrainLayoutRepository trainLayoutRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TrainService(TrainRepository trainRepository, ProviderRepository providerRepository,
            TrainLayoutRepository trainLayoutRepository) {
        this.trainRepository = trainRepository;
        this.providerRepository = providerRepository;
        this.trainLayoutRepository = trainLayoutRepository;
    }

    public List<TrainDTO> getAllTrains() {
        return trainRepository.findAll().stream()
                .map(t -> new TrainDTO(t.getId(), t.getName(),
                        t.getProvider() != null ? t.getProvider().getId() : null,
                        t.getProvider() != null ? t.getProvider().getName() : null,
                        t.getTrainLayout() != null ? t.getTrainLayout().getId() : null,
                        t.getTrainLayout() != null ? t.getTrainLayout().getLayoutName() : null,
                        t.getTotalSeats()))
                .collect(Collectors.toList());
    }

    public TrainDTO getTrainById(Long id) {
        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Train not found"));
        return new TrainDTO(train.getId(), train.getName(),
                train.getProvider() != null ? train.getProvider().getId() : null,
                train.getProvider() != null ? train.getProvider().getName() : null,
                train.getTrainLayout() != null ? train.getTrainLayout().getId() : null,
                train.getTrainLayout() != null ? train.getTrainLayout().getLayoutName() : null,
                train.getTotalSeats());
    }

    public TrainDTO createTrain(TrainDTO trainDTO) {
        Train train = new Train();
        train.setName(trainDTO.getName());

        if (trainDTO.getProviderId() != null) {
            Provider provider = providerRepository.findById(trainDTO.getProviderId())
                    .orElseThrow(() -> new RuntimeException("Provider not found"));
            train.setProvider(provider);
        }

        if (trainDTO.getTrainLayoutId() != null) {
            TrainLayout layout = trainLayoutRepository.findById(trainDTO.getTrainLayoutId())
                    .orElseThrow(() -> new RuntimeException("Layout not found"));
            train.setTrainLayout(layout);

            // Generate seats from layout
            try {
                int[][] grid = objectMapper.readValue(layout.getLayoutConfig(), int[][].class);
                List<Seat> seats = new ArrayList<>();

                for (int r = 0; r < grid.length; r++) {
                    for (int c = 0; c < grid[r].length; c++) {
                        if (grid[r][c] == 1) { // 1 is a Seat
                            Seat seat = new Seat();
                            // Generate seat number like "1A", "1B", etc.
                            char colChar = (char) ('A' + c);
                            String seatNum = (r + 1) + String.valueOf(colChar);

                            seat.setNumber(seatNum);
                            seat.setAvailable(true);
                            seat.setTrain(train);
                            seats.add(seat);
                        }
                    }
                }
                train.setSeats(seats);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse layout config", e);
            }

        } else if (trainDTO.getTotalSeats() > 0) {
            List<Seat> seats = new ArrayList<>();
            for (int i = 1; i <= trainDTO.getTotalSeats(); i++) {
                Seat seat = new Seat();
                seat.setNumber(String.valueOf(i));
                seat.setAvailable(true);
                seat.setTrain(train);
                seats.add(seat);
            }
            train.setSeats(seats);
        }

        Train savedTrain = trainRepository.save(train);
        return new TrainDTO(savedTrain.getId(), savedTrain.getName(),
                savedTrain.getProvider() != null ? savedTrain.getProvider().getId() : null,
                savedTrain.getProvider() != null ? savedTrain.getProvider().getName() : null,
                savedTrain.getTrainLayout() != null ? savedTrain.getTrainLayout().getId() : null,
                savedTrain.getTrainLayout() != null ? savedTrain.getTrainLayout().getLayoutName() : null,
                savedTrain.getTotalSeats());
    }

    public TrainDTO updateTrain(Long id, TrainDTO trainDTO) {
        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Train not found"));

        train.setName(trainDTO.getName());

        if (trainDTO.getProviderId() != null) {
            Provider provider = providerRepository.findById(trainDTO.getProviderId())
                    .orElseThrow(() -> new RuntimeException("Provider not found"));
            train.setProvider(provider);
        } else {
            train.setProvider(null);
        }

        if (trainDTO.getTrainLayoutId() != null) {
            TrainLayout layout = trainLayoutRepository.findById(trainDTO.getTrainLayoutId())
                    .orElseThrow(() -> new RuntimeException("Layout not found"));
            train.setTrainLayout(layout);
            // Note: We are not regenerating seats on update to preserve existing
            // bookings/data.
            // If seat regeneration is needed, it should be a separate explicit action or
            // handle with care.
        } else {
            train.setTrainLayout(null);
        }

        Train savedTrain = trainRepository.save(train);
        return new TrainDTO(savedTrain.getId(), savedTrain.getName(),
                savedTrain.getProvider() != null ? savedTrain.getProvider().getId() : null,
                savedTrain.getProvider() != null ? savedTrain.getProvider().getName() : null,
                savedTrain.getTrainLayout() != null ? savedTrain.getTrainLayout().getId() : null,
                savedTrain.getTrainLayout() != null ? savedTrain.getTrainLayout().getLayoutName() : null,
                savedTrain.getTotalSeats());
    }

    public void deleteTrain(Long id) {
        trainRepository.deleteById(id);
    }
}
