package nl.novi.LivingInSync.repository;

import nl.novi.LivingInSync.model.Cycle;
import nl.novi.LivingInSync.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CycleRepository extends JpaRepository<Cycle, Long> {

    Optional<Cycle> findByCycleUser(User user);
}