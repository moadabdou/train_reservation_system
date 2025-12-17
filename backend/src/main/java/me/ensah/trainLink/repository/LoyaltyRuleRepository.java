package me.ensah.trainLink.repository;

import me.ensah.trainLink.model.LoyaltyRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoyaltyRuleRepository extends JpaRepository<LoyaltyRule, Long> {
    Optional<LoyaltyRule> findByRuleName(String ruleName);
}
