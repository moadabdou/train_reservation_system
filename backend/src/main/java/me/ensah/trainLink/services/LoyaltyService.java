package me.ensah.trainLink.services;

import me.ensah.trainLink.DTO.LoyaltyStatusDTO;
import me.ensah.trainLink.model.LoyaltyPoints;
import me.ensah.trainLink.model.Reward;
import me.ensah.trainLink.model.User;
import me.ensah.trainLink.repository.LoyaltyPointsRepository;
import me.ensah.trainLink.repository.RewardRepository;
import me.ensah.trainLink.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class LoyaltyService {

    private final LoyaltyPointsRepository loyaltyPointsRepository;
    private final RewardRepository rewardRepository;
    private final UserRepository userRepository;

    public LoyaltyService(LoyaltyPointsRepository loyaltyPointsRepository, RewardRepository rewardRepository,
            UserRepository userRepository) {
        this.loyaltyPointsRepository = loyaltyPointsRepository;
        this.rewardRepository = rewardRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public void awardPoints(User user, double amountSpent) {
        LoyaltyPoints points = loyaltyPointsRepository.findByUser(user)
                .orElseGet(() -> {
                    LoyaltyPoints newPoints = new LoyaltyPoints();
                    newPoints.setUser(user);
                    return newPoints;
                });

        int pointsEarned = (int) amountSpent; // 1 point per unit currency
        points.setPointsBalance(points.getPointsBalance() + pointsEarned);
        updateTier(points);
        loyaltyPointsRepository.save(points);
    }

    private void updateTier(LoyaltyPoints points) {
        int balance = points.getPointsBalance();
        if (balance >= 2000) {
            points.setTierLevel("PLATINUM");
        } else if (balance >= 1000) {
            points.setTierLevel("GOLD");
        } else if (balance >= 500) {
            points.setTierLevel("SILVER");
        } else {
            points.setTierLevel("BRONZE");
        }
    }

    public LoyaltyStatusDTO getMyStatus() {
        User user = getCurrentUser();
        LoyaltyPoints points = loyaltyPointsRepository.findByUser(user)
                .orElseGet(() -> {
                    LoyaltyPoints newPoints = new LoyaltyPoints();
                    newPoints.setUser(user);
                    newPoints.setPointsBalance(0);
                    newPoints.setTierLevel("BRONZE");
                    return loyaltyPointsRepository.save(newPoints);
                });

        LoyaltyStatusDTO dto = new LoyaltyStatusDTO();
        dto.setPointsBalance(points.getPointsBalance());
        dto.setTierLevel(points.getTierLevel());

        calculateNextTier(dto);

        return dto;
    }

    private void calculateNextTier(LoyaltyStatusDTO dto) {
        int balance = dto.getPointsBalance();
        if (balance < 500) {
            dto.setNextTier("SILVER");
            dto.setPointsToNextTier(500 - balance);
        } else if (balance < 1000) {
            dto.setNextTier("GOLD");
            dto.setPointsToNextTier(1000 - balance);
        } else if (balance < 2000) {
            dto.setNextTier("PLATINUM");
            dto.setPointsToNextTier(2000 - balance);
        } else {
            dto.setNextTier("MAX");
            dto.setPointsToNextTier(0);
        }
    }

    public List<Reward> getAllRewards() {
        return rewardRepository.findAll();
    }

    @Transactional
    public String redeemReward(Long rewardId) {
        User user = getCurrentUser();
        LoyaltyPoints points = loyaltyPointsRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Loyalty profile not found"));

        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("Reward not found"));

        if (points.getPointsBalance() < reward.getCostInPoints()) {
            throw new RuntimeException("Insufficient points");
        }

        points.setPointsBalance(points.getPointsBalance() - reward.getCostInPoints());
        // Recalculate tier? Usually tiers are based on lifetime points or points earned
        // in a period,
        // but for simplicity let's say spending points doesn't downgrade tier
        // immediately,
        // OR let's say it does. The prompt doesn't specify.
        // I'll keep the tier as is, assuming it's based on accumulation or status.
        // But wait, if I just subtract points, `updateTier` might downgrade them if I
        // called it.
        // I won't call updateTier here.

        loyaltyPointsRepository.save(points);

        // Generate a voucher code
        return "VOUCHER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
