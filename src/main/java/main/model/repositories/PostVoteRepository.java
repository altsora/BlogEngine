package main.model.repositories;

import main.model.entities.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostVoteRepository extends JpaRepository<PostVote, Long> {

    @Query("SELECT COUNT(*) FROM PostVote pv WHERE pv.post.id = :postId AND pv.value = 1")
    int getCountLikesByPostId(@Param("postId") Integer postId);

    @Query("SELECT COUNT(*) FROM PostVote pv WHERE pv.post.id = :postId AND pv.value = -1")
    int getCountDislikesByPostId(@Param("postId") Integer postId);

}
