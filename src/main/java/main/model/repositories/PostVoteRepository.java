package main.model.repositories;

import main.model.entities.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostVoteRepository extends JpaRepository<PostVote, Long> {
    @Query("SELECT u FROM #{#entityName} u WHERE u.post.id = :postId")
    PostVote findByPostId(@Param("postId") Integer postId);

    @Query("SELECT u FROM #{#entityName} u WHERE u.user.id = :userId AND u.post.id = :postId")
    PostVote findByUserIdAndPostId(
            @Param("userId") Integer userId,
            @Param("postId") Integer postId
    );

    @Query("SELECT u FROM #{#entityName} u WHERE u.post.id = :postId")
    List<PostVote> findAllPostVotesByPostId(@Param("postId") Integer postId);

    @Query("SELECT COUNT(*) FROM PostVote pv WHERE pv.post.id = :postId AND pv.value = 1")
    int getCountLikesByPostId(@Param("postId") Integer postId);

    @Query("SELECT COUNT(*) FROM PostVote pv WHERE pv.post.id = :postId AND pv.value = -1")
    int getCountDislikesByPostId(@Param("postId") Integer postId);

}
