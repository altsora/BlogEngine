package main.model.repositories;

import main.model.entities.enums.ModerationStatusType;
import main.model.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   (now() - p.time) >= 0 " +
            "ORDER BY p.time DESC")
    List<Post> findAllPostRecent(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatusType moderationStatusType
    );

    @Query("SELECT p FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   (now() - p.time) >= 0 " +
            "ORDER BY p.time ASC")
    List<Post> findAllPostEarly(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatusType moderationStatusType
    );

    @Query("SELECT p " +
            "FROM Post p LEFT JOIN PostComment pc " +
            "ON pc.post.id = p.id " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   (now() - p.time) >= 0 " +
            "GROUP BY p.id ORDER BY COUNT(*) DESC")
    List<Post> findAllPostPopular(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatusType moderationStatusType
    );

    @Query("SELECT p, (SELECT COUNT(*) FROM PostVote pv WHERE pv.post.id = p.id AND pv.value = 1) " +
            "FROM Post p LEFT JOIN PostVote pv " +
            "ON pv.post.id = p.id " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   (now() - p.time) >= 0 " +
            "GROUP BY p.id ORDER BY 2 DESC")
    List<Post> findAllPostBest(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatusType moderationStatusType
    );

    @Query("SELECT p FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   (now() - p.time) >= 0 AND " +
            "   p.title LIKE %:query% OR p.text LIKE %:query% " +
            "ORDER BY p.time DESC")
    List<Post> findAllPostRecentByQuery(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatusType moderationStatusType,
            @Param("query") String query
    );

    @Query("SELECT p FROM Post p " +
            "WHERE " +
            "   p.id = :postId AND" +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   (now() - p.time) >= 0")
    Post findPostById(
            @Param("postId") Integer postId,
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatusType moderationStatusType
    );

    @Query("SELECT p FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   YEAR(p.time) = :year AND " +
            "   MONTH(p.time) = :month AND " +
            "   DAYOFMONTH(p.time) = :dayOfMonth")
    List<Post> findAllPostByDate(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatusType moderationStatusType,
            @Param("year") int year,
            @Param("month") int month,
            @Param("dayOfMonth") int dayOfMonth
    );
}
