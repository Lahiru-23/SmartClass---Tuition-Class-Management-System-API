package lk.SmartClass.repository;

import lk.SmartClass.entity.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuardianRepository extends JpaRepository<Guardian, Long> {
    Optional<Guardian> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}