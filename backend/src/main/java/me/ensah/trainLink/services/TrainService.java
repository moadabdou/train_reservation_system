package me.ensah.trainLink.services;

import me.ensah.trainLink.DTO.TrainDTO;
import me.ensah.trainLink.model.Provider;
import me.ensah.trainLink.model.Train;
import me.ensah.trainLink.repository.ProviderRepository;
import me.ensah.trainLink.repository.TrainRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainService {

    private final TrainRepository trainRepository;
    private final ProviderRepository providerRepository;

    public TrainService(TrainRepository trainRepository, ProviderRepository providerRepository) {
        this.trainRepository = trainRepository;
        this.providerRepository = providerRepository;
    }

    public List<TrainDTO> getAllTrains() {
        return trainRepository.findAll().stream()
                .map(t -> new TrainDTO(t.getId(), t.getName(),
                        t.getProvider() != null ? t.getProvider().getId() : null,
                        t.getProvider() != null ? t.getProvider().getName() : null))
                .collect(Collectors.toList());
    }

    public TrainDTO getTrainById(Long id) {
        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Train not found"));
        return new TrainDTO(train.getId(), train.getName(),
                train.getProvider() != null ? train.getProvider().getId() : null,
                train.getProvider() != null ? train.getProvider().getName() : null);
    }

    public TrainDTO createTrain(TrainDTO trainDTO) {
        Train train = new Train();
        train.setName(trainDTO.getName());

        if (trainDTO.getProviderId() != null) {
            Provider provider = providerRepository.findById(trainDTO.getProviderId())
                    .orElseThrow(() -> new RuntimeException("Provider not found"));
            train.setProvider(provider);
        }

        Train savedTrain = trainRepository.save(train);
        return new TrainDTO(savedTrain.getId(), savedTrain.getName(),
                savedTrain.getProvider() != null ? savedTrain.getProvider().getId() : null,
                savedTrain.getProvider() != null ? savedTrain.getProvider().getName() : null);
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

        Train savedTrain = trainRepository.save(train);
        return new TrainDTO(savedTrain.getId(), savedTrain.getName(),
                savedTrain.getProvider() != null ? savedTrain.getProvider().getId() : null,
                savedTrain.getProvider() != null ? savedTrain.getProvider().getName() : null);
    }

    public void deleteTrain(Long id) {
        trainRepository.deleteById(id);
    }
}
