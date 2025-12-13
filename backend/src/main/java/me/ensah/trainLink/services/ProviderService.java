package me.ensah.trainLink.services;

import me.ensah.trainLink.DTO.ProviderDTO;
import me.ensah.trainLink.model.Provider;
import me.ensah.trainLink.repository.ProviderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProviderService {

    private final ProviderRepository providerRepository;

    public ProviderService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    public List<ProviderDTO> getAllProviders() {
        return providerRepository.findAll().stream()
                .map(p -> new ProviderDTO(p.getId(), p.getName(), p.getLogoUrl(), p.getContactInfo()))
                .collect(Collectors.toList());
    }

    public ProviderDTO getProviderById(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found"));
        return new ProviderDTO(provider.getId(), provider.getName(), provider.getLogoUrl(), provider.getContactInfo());
    }

    public ProviderDTO createProvider(ProviderDTO providerDTO) {
        Provider provider = new Provider();
        provider.setName(providerDTO.getName());
        provider.setLogoUrl(providerDTO.getLogoUrl());
        provider.setContactInfo(providerDTO.getContactInfo());

        Provider savedProvider = providerRepository.save(provider);
        return new ProviderDTO(savedProvider.getId(), savedProvider.getName(), savedProvider.getLogoUrl(),
                savedProvider.getContactInfo());
    }

    public ProviderDTO updateProvider(Long id, ProviderDTO providerDTO) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        provider.setName(providerDTO.getName());
        provider.setLogoUrl(providerDTO.getLogoUrl());
        provider.setContactInfo(providerDTO.getContactInfo());

        Provider savedProvider = providerRepository.save(provider);
        return new ProviderDTO(savedProvider.getId(), savedProvider.getName(), savedProvider.getLogoUrl(),
                savedProvider.getContactInfo());
    }

    public void deleteProvider(Long id) {
        providerRepository.deleteById(id);
    }
}
