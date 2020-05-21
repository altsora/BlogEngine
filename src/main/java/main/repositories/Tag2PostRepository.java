package main.repositories;

import main.model.entities.Tag2Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Tag2PostRepository extends JpaRepository<Tag2Post, Long> {

    @Query("SELECT tp FROM Tag2Post tp WHERE tp.post.id = :postId ORDER BY tp.tag.id")
    List<Tag2Post> findAllTag2PostByPostId(@Param("postId") int postId);
}
