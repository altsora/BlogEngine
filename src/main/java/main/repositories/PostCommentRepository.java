package main.repositories;

import main.model.entities.PostComment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    String COMMENT_TIME = "time";

    @Query("SELECT pc FROM PostComment pc WHERE pc.post.id = :postId")
    List<PostComment> findAllPostCommentByPostId(@Param("postId") int postId, Sort sort);

    @Query("SELECT COUNT(*) FROM PostComment pc WHERE pc.post.id = :postId")
    int getCountCommentsByPostId(@Param("postId") int postId);
}
