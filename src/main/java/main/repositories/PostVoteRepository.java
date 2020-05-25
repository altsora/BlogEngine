package main.repositories;

import main.model.entities.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostVoteRepository extends JpaRepository<PostVote, Long> {

    /**
     * Запрос возвращает количество лайков указанного поста.
     * @param postId - ID поста;
     * @return - возвращает количество лайков указанного поста.
     */
    @Query("SELECT COUNT(*) FROM PostVote pv " +
            "WHERE " +
            "   pv.post.id = :postId AND " +
            "   pv.value = 1")
    int getCountLikesByPostId(@Param("postId") int postId);

    /**
     * Запрос возвращает количество дизлайков указанного поста.
     * @param postId - ID поста;
     * @return - возвращает количество дизлайков указанного поста.
     */
    @Query("SELECT COUNT(*) FROM PostVote pv " +
            "WHERE " +
            "   pv.post.id = :postId AND " +
            "   pv.value = -1")
    int getCountDislikesByPostId(@Param("postId") int postId);

    /**
     * Возвращает общее количество лайков.
     * @return - целое число, равное количеству лайков.
     */
    @Query("SELECT COUNT(*) FROM PostVote pv WHERE pv.value = 1")
    int getTotalCountLikes();

    /**
     * Возвращает общее количество дизлайков.
     * @return - целое число, равное количеству дизлайков.
     */
    @Query("SELECT COUNT(*) FROM PostVote pv WHERE pv.value = -1")
    int getTotalCountDislikes();

    /**
     * Запрос возвращает общее количество лайков под постами указанного пользователя.
     * @param userId - ID пользователя.
     * @return - целое число, равное количеству лайков определённого пользователя.
     */
    @Query("SELECT COUNT(pv.value) FROM PostVote pv " +
            "JOIN Post p ON p.id = pv.post.id " +
            "WHERE " +
            "   p.user.id = :userId AND " +
            "   pv.value = 1")
    int getTotalCountLikesByUserId(@Param("userId") int userId);

    /**
     * Запрос возвращает общее количество дизлайков под постами указанного пользователя.
     * @param userId - ID пользователя.
     * @return - целое число, равное количеству дизлайков определённого пользователя.
     */
    @Query("SELECT COUNT(pv.value) FROM PostVote pv " +
            "JOIN Post p ON p.id = pv.post.id " +
            "WHERE " +
            "   p.user.id = :userId AND " +
            "   pv.value = -1")
    int getTotalCountDislikesByUserId(@Param("userId") int userId);

    /**
     * Запрос возвращает положительную оценку по идентификатору пользователя и идентификатору поста.
     * @param userId - ID пользователя;
     * @param postId - ID поста.
     * @return - возвращает положительную оценку класса PostVote. Если такой оценки не найдено, вернётся null.
     */
    @Query("SELECT pv FROM PostVote pv " +
            "WHERE " +
            "   pv.user.id = :userId AND " +
            "   pv.post.id = :postId AND " +
            "   pv.value = 1")
    PostVote userLikeAlreadyExists(
            @Param("userId") int userId,
            @Param("postId") int postId
    );

    /**
     * Запрос возвращает отрицательную оценку по идентификатору пользователя и идентификатору поста.
     * @param userId - ID пользователя;
     * @param postId - ID поста.
     * @return - возвращает отрицательную оценку класса PostVote. Если такой оценки не найдено, вернётся null.
     */
    @Query("SELECT pv FROM PostVote pv " +
            "WHERE " +
            "   pv.user.id = :userId AND " +
            "   pv.post.id = :postId AND " +
            "   pv.value = -1")
    PostVote userDislikeAlreadyExists(
            @Param("userId") int userId,
            @Param("postId") int postId
    );

    @Query("SELECT pv.id FROM PostVote pv " +
            "WHERE " +
            "   pv.user.id = :userId AND " +
            "   pv.post.id = :postId")
    Integer getPostVoteIdByUserIdAndPostId(
            @Param("userId") int userId,
            @Param("postId") int postId
    );
}
