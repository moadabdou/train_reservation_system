package me.ensah.trainLink.services;

import me.ensah.trainLink.model.LoyaltyRule;
import me.ensah.trainLink.model.Reward;
import me.ensah.trainLink.repository.LoyaltyRuleRepository;
import me.ensah.trainLink.repository.RewardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminLoyaltyService {

    @Autowired
    private RewardRepository rewardRepository;

    @Autowired
    private LoyaltyRuleRepository loyaltyRuleRepository;

    // --- Rewards Management ---

    public List<Reward> getAllRewards() {
        return rewardRepository.findAll();
    }

    public Reward createReward(Reward reward) {
        return rewardRepository.save(reward);
    }

    public Reward updateReward(Long id, Reward updatedReward) {
        return rewardRepository.findById(id).map(reward -> {
            reward.setDescription(updatedReward.getDescription());
            reward.setCostInPoints(updatedReward.getCostInPoints());
            reward.setDiscountValue(updatedReward.getDiscountValue());
            reward.setType(updatedReward.getType());
            return rewardRepository.save(reward);
        }).orElseThrow(() -> new RuntimeException("Reward not found"));
    }

    public void deleteReward(Long id) {
        rewardRepository.deleteById(id);
    }

    // --- Loyalty Rules Management ---

    public List<LoyaltyRule> getAllRules() {
        // Ensure default rules exist
        ensureDefaultRule("POINTS_PER_DH", 1.0, "Points earned per 1 DH spent");
        ensureDefaultRule("SIGNUP_BONUS", 500.0, "Points awarded upon registration");
        return loyaltyRuleRepository.findAll();
    }

    public LoyaltyRule updateRule(String ruleName, Double value) {
        LoyaltyRule rule = loyaltyRuleRepository.findByRuleName(ruleName)
                .orElseThrow(() -> new RuntimeException("Rule not found: " + ruleName));
        rule.setValue(value);
        return loyaltyRuleRepository.save(rule);
    }

    private void ensureDefaultRule(String name, Double defaultValue, String description) {
        if (loyaltyRuleRepository.findByRuleName(name).isEmpty()) {
            LoyaltyRule rule = new LoyaltyRule();
            rule.setRuleName(name);
            rule.setValue(defaultValue);
            rule.setDescription(description);
            loyaltyRuleRepository.save(rule);
        }
    }
}
