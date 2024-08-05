package nl.novi.LivingInSync.repository;

import nl.novi.LivingInSync.model.Phase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhaseRepository extends JpaRepository<Phase, Long> {
    List<Phase> findByCycleId(Long cycleId);
}