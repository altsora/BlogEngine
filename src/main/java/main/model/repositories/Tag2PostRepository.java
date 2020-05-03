package main.model.repositories;

import main.model.entities.Tag2Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Tag2PostRepository extends JpaRepository<Tag2Post, Long> {
}
