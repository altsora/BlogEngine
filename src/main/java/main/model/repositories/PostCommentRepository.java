package main.model.repositories;

import main.model.entities.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    @Query("SELECT u FROM #{#entityName} u WHERE u.post.id = :postId")
    List<PostComment> findAllPostCommentByPostId(@Param("postId") Integer postId);

    @Query("SELECT COUNT(*) FROM PostComment pc WHERE pc.post.id = :postId")
    int getCountCommentsByPostId(@Param("postId") Integer postId);
}
