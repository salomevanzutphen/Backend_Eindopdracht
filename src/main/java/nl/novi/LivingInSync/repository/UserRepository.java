package nl.novi.LivingInSync.repository;

import nl.novi.LivingInSync.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


//you should see a list of all users and filter based on their role
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);

    boolean existsByUsername(String admin);
}