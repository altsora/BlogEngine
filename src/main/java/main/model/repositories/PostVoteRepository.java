package main.model.repositories;

import main.model.entities.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostVoteRepository extends JpaRepository<PostVote, Long> {
}
