package main.model.repositories;

import main.model.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT u FROM #{#entityName} u ORDER BY u.time DESC")
    List<Post> findAllPostRecent();

    @Query("SELECT u FROM #{#entityName} u ORDER BY u.time ASC")
    List<Post> findAllPostEarly();

    @Query("SELECT u FROM #{#entityName} u ORDER BY u.viewCount DESC")
    List<Post> findAllPostPopular();

    @Query(
            value = "SELECT * FROM POSTS p " +
                    "INNER JOIN POST_VOTES pv " +
                    "ON p.id = pv.post_id",
            nativeQuery = true)
    List<Post> test();

}
