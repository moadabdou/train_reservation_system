package me.ensah.trainLink.controller;

import me.ensah.trainLink.model.CityGuide;
import me.ensah.trainLink.model.OnboardItem;
import me.ensah.trainLink.services.AdminContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/content")
@PreAuthorize("hasRole('ADMIN')")
public class AdminContentController {

    @Autowired
    private AdminContentService adminContentService;

    // --- City Guides ---

    @GetMapping("/cities")
    public ResponseEntity<List<CityGuide>> getAllCityGuides() {
        return ResponseEntity.ok(adminContentService.getAllCityGuides());
    }

    @PostMapping("/cities")
    public ResponseEntity<CityGuide> createCityGuide(@RequestBody CityGuide guide) {
        return ResponseEntity.ok(adminContentService.saveCityGuide(guide));
    }

    @PutMapping("/cities/{id}")
    public ResponseEntity<CityGuide> updateCityGuide(@PathVariable Long id, @RequestBody CityGuide guide) {
        return ResponseEntity.ok(adminContentService.updateCityGuide(id, guide));
    }

    @DeleteMapping("/cities/{id}")
    public ResponseEntity<Void> deleteCityGuide(@PathVariable Long id) {
        adminContentService.deleteCityGuide(id);
        return ResponseEntity.noContent().build();
    }

    // --- Onboard Items ---

    @GetMapping("/items")
    public ResponseEntity<List<OnboardItem>> getAllOnboardItems() {
        return ResponseEntity.ok(adminContentService.getAllOnboardItems());
    }

    @PostMapping("/items")
    public ResponseEntity<OnboardItem> createOnboardItem(@RequestBody OnboardItem item) {
        return ResponseEntity.ok(adminContentService.saveOnboardItem(item));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<OnboardItem> updateOnboardItem(@PathVariable Long id, @RequestBody OnboardItem item) {
        return ResponseEntity.ok(adminContentService.updateOnboardItem(id, item));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteOnboardItem(@PathVariable Long id) {
        adminContentService.deleteOnboardItem(id);
        return ResponseEntity.noContent().build();
    }
}
