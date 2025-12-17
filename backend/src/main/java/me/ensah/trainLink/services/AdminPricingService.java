package me.ensah.trainLink.services;

import me.ensah.trainLink.model.PricingRule;
import me.ensah.trainLink.model.TrainLayout;
import me.ensah.trainLink.repository.PricingRuleRepository;
import me.ensah.trainLink.repository.TrainLayoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminPricingService {

    @Autowired
    private TrainLayoutRepository trainLayoutRepository;

    @Autowired
    private PricingRuleRepository pricingRuleRepository;

    // --- Train Layouts ---

    public List<TrainLayout> getAllLayouts() {
        return trainLayoutRepository.findAll();
    }

    public TrainLayout saveLayout(TrainLayout layout) {
        return trainLayoutRepository.save(layout);
    }

    public void deleteLayout(Long id) {
        trainLayoutRepository.deleteById(id);
    }

    // --- Pricing Rules ---

    public List<PricingRule> getAllRules() {
        return pricingRuleRepository.findAll();
    }

    public PricingRule saveRule(PricingRule rule) {
        return pricingRuleRepository.save(rule);
    }

    public void deleteRule(Long id) {
        pricingRuleRepository.deleteById(id);
    }
}
