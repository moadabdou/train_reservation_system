package me.ensah.trainLink.services;

import me.ensah.trainLink.model.CityGuide;
import me.ensah.trainLink.model.OnboardItem;
import me.ensah.trainLink.repository.CityGuideRepository;
import me.ensah.trainLink.repository.OnboardItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminContentService {

    @Autowired
    private CityGuideRepository cityGuideRepository;

    @Autowired
    private OnboardItemRepository onboardItemRepository;

    // --- City Guides ---

    public List<CityGuide> getAllCityGuides() {
        return cityGuideRepository.findAll();
    }

    public CityGuide saveCityGuide(CityGuide guide) {
        return cityGuideRepository.save(guide);
    }

    public CityGuide updateCityGuide(Long id, CityGuide updatedGuide) {
        return cityGuideRepository.findById(id).map(guide -> {
            guide.setCityName(updatedGuide.getCityName());
            guide.setContent(updatedGuide.getContent());
            guide.setWeatherApiId(updatedGuide.getWeatherApiId());
            return cityGuideRepository.save(guide);
        }).orElseThrow(() -> new RuntimeException("City Guide not found"));
    }

    public void deleteCityGuide(Long id) {
        cityGuideRepository.deleteById(id);
    }

    // --- Onboard Items ---

    public List<OnboardItem> getAllOnboardItems() {
        return onboardItemRepository.findAll();
    }

    public OnboardItem saveOnboardItem(OnboardItem item) {
        return onboardItemRepository.save(item);
    }

    public OnboardItem updateOnboardItem(Long id, OnboardItem updatedItem) {
        return onboardItemRepository.findById(id).map(item -> {
            item.setName(updatedItem.getName());
            item.setPrice(updatedItem.getPrice());
            item.setCategory(updatedItem.getCategory());
            item.setAvailable(updatedItem.isAvailable());
            return onboardItemRepository.save(item);
        }).orElseThrow(() -> new RuntimeException("Item not found"));
    }

    public void deleteOnboardItem(Long id) {
        onboardItemRepository.deleteById(id);
    }
}
