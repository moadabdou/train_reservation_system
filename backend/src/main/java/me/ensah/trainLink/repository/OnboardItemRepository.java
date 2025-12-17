package me.ensah.trainLink.repository;

import me.ensah.trainLink.model.OnboardItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OnboardItemRepository extends JpaRepository<OnboardItem, Long> {
}
