package nl.novi.LivingInSync.repository;

import nl.novi.LivingInSync.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
}
