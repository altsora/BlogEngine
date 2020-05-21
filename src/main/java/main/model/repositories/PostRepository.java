package main.model.repositories;

import main.model.entities.enums.ModerationStatusType;
import main.model.entities.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.Tuple;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    String COUNT_COMMENTS = "countComments";
    String COUNT_LIKES = "countLikes";
    String POST_TIME = "time";

    //=============================================================================

    @Query("SELECT COUNT(p) FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   p.time <= now()")
    int getTotalNumberOfPosts(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatusType moderationStatusType
    );

    @Query("SELECT COUNT(p) FROM Post p " +
            "LEFT JOIN Tag2Post tp ON p.id = tp.post.id " +
            "LEFT JOIN Tag t ON t.id = tp.tag.id " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   p.time <= now() AND " +
            "   t.name = :tag")
    int getTotalNumberOfPostsByTag(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatusType moderationStatusType,
            @Param("tag") String tag
    );

    @Query("SELECT COUNT(p) FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   YEAR(p.time) = :year AND " +
            "   MONTH(p.time) = :month AND " +
            "   DAYOFMONTH(p.time) = :dayOfMonth")
    int getTotalNumberOfPostsByDate(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatusType moderationStatusType,
            @Param("year") int year,
            @Param("month") int month,
            @Param("dayOfMonth") int dayOfMonth
    );

    @Query("SELECT COUNT(p) FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   p.time <= now() AND " +
            "   p.title LIKE %:query% OR p.text LIKE %:query% ")
    int getTotalNumberOfPostsByQuery(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatusType moderationStatusType,
            @Param("query") String query
    );

    //=============================================================================

    @Query("SELECT p.time, COUNT(p) " +
            "FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   p.time <= now() AND " +
            "   YEAR(p.time) = :year " +
            "GROUP BY YEAR(p.time), MONTH(p.time), DAYOFMONTH(p.time)")
    List<Tuple> getDateAndCountPosts(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatusType moderationStatusType,
            @Param("year") int year,
            Sort sort
    );

    @Query("SELECT YEAR(p.time) AS yearPost " +
            "FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   p.time <= now() " +
            "GROUP BY yearPost")
    List<Integer> findAllYearsOfPublication(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatusType moderationStatusType,
            Sort sort
    );

    //=============================================================================

    @Query("SELECT p FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   p.time <= now() ")
    List<Post> findAllPostSortedByDate(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatusType moderationStatusType,
            Pageable pageable
    );

    @Query("SELECT p, COUNT(pc) AS " + COUNT_COMMENTS + " " +
            "FROM Post p LEFT JOIN PostComment pc " +
            "ON pc.post.id = p.id " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   p.time <= now() " +
            "GROUP BY p.id")
    List<Post> findAllPostPopular(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatusType moderationStatusType,
            Pageable pageable
    );


    @Query("SELECT p, " +
            "   (SELECT COUNT(pv) AS countLikes FROM PostVote pv WHERE pv.post.id = p.id AND pv.value = 1) " +
            "   AS " + COUNT_LIKES + " " +
            "FROM Post p LEFT JOIN PostVote pv " +
            "ON pv.post.id = p.id " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   p.time <= now() " +
            "GROUP BY p.id")
    List<Post> findAllPostBest(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatusType moderationStatusType,
            Pageable pageable
    );

    //=============================================================================

    @Query("SELECT p FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   p.time <= now() AND " +
            "   p.title LIKE %:query% OR p.text LIKE %:query% ")
    List<Post> findAllPostByQuery(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatusType moderationStatusType,
            @Param("query") String query,
            Pageable pageable
    );

    //=============================================================================

    @Query("SELECT p FROM Post p " +
            "WHERE " +
            "   p.id = :postId AND" +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   p.time <= now()")
    Post findPostById(
            @Param("postId") int postId,
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatusType moderationStatusType
    );

    //=============================================================================

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
            @Param("dayOfMonth") int dayOfMonth,
            Pageable pageable
    );

    //=============================================================================

    @Query("SELECT p FROM Post p " +
            "LEFT JOIN Tag2Post tp ON p.id = tp.post.id " +
            "LEFT JOIN Tag t ON t.id = tp.tag.id " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   p.time <= now() AND " +
            "   t.name = :tag")
    List<Post> findAllPostByTag(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatusType moderationStatusType,
            @Param("tag") String tag,
            Pageable pageable
    );

}
