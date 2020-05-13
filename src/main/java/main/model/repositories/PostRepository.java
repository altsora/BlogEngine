package main.model.repositories;

import main.model.ModerationStatusType;
import main.model.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p " +
            "WHERE p.isActive = :isActive AND p.moderationStatus = :moderationStatus " +
            "ORDER BY p.time DESC")
    List<Post> findAllPostRecent(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatusType moderationStatusType
    );

    @Query("SELECT p FROM Post p " +
            "WHERE p.isActive = :isActive AND p.moderationStatus = :moderationStatus " +
            "ORDER BY p.time ASC")
    List<Post> findAllPostEarly(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatusType moderationStatusType
    );

    @Query("SELECT p, COUNT(*) " +
            "FROM Post p LEFT JOIN PostComment pc " +
            "ON pc.post.id = p.id " +
            "WHERE p.isActive = :isActive AND p.moderationStatus = :moderationStatus " +
            "GROUP BY p.id ORDER BY 2 DESC")
    List<Post> findAllPostPopular(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatusType moderationStatusType
    );

    @Query(value = "SELECT p, SUM(pv.value) " +
            "FROM Post p LEFT JOIN PostVote pv " +
            "ON pv.post.id = p.id " +
            "WHERE p.isActive = :isActive AND p.moderationStatus = :moderationStatus " +
            "GROUP BY p.id ORDER BY 2 DESC")
    List<Post> findAllPostBest(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatusType moderationStatusType
    );
}
