package main.repositories;

import main.model.entity.PostVote;
import main.model.enums.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostVoteRepository extends JpaRepository<PostVote, Long> {

    /**
     * Запрос возвращает количество лайков или дизлайков указанного поста.
     * @param postId - ID поста;
     * @param value - значение, по которому подсчитываются количество постов.
     * @return - возвращает количество конкретных оценок указанного поста.
     */
    @Query("SELECT COUNT(*) FROM PostVote pv " +
            "WHERE " +
            "   pv.post.id = :postId AND " +
            "   pv.value = :value")
    int getCountRatingByPostId(
            @Param("postId") long postId,
            @Param("value") Rating value
    );

    /**
     * Возвращает общее количество лайков или дизлайков.
     * @param value - значение оценки, по которой осуществляется поиск;
     * @return - целое число, равное количеству лайков или дизлайков.
     */
    @Query("SELECT COUNT(*) FROM PostVote pv WHERE pv.value = :value")
    int getTotalCountRating(@Param("value") Rating value);

    /**
     * Запрос возвращает общее количество лайков или дизлайков под постами указанного пользователя.
     * @param userId - идентификатор пользователя;
     * @param value - значение оценки, по которой осуществляется поиск;
     * @return - целое число, равное количеству лайков или дизлайков определённого пользователя.
     */
    @Query("SELECT COUNT(pv.value) FROM PostVote pv " +
            "JOIN Post p ON p.id = pv.post.id " +
            "WHERE " +
            "   p.user.id = :userId AND " +
            "   pv.value = :value")
    int getTotalCountRatingByUserId(
            @Param("userId") long userId,
            @Param("value") Rating value
    );

    /**
     * Запрос возвращает положительную или отрицательную оценку по идентификатору пользователя и идентификатору поста.
     * @param userId - ID пользователя;
     * @param postId - ID поста;
     * @param value - значение оценки, по которой осуществляется поиск;
     * @return  - возвращает положительную или отрицательную оценку класса PostVote.
     * Если такой оценки не найдено, вернётся null.
     */
    @Query("SELECT pv FROM PostVote pv " +
            "WHERE " +
            "   pv.user.id = :userId AND " +
            "   pv.post.id = :postId AND " +
            "   pv.value = :value")
    PostVote ratingUserAlreadyExists(
            @Param("userId") long userId,
            @Param("postId") long postId,
            @Param("value") Rating value
    );

    @Query("SELECT pv.id FROM PostVote pv " +
            "WHERE " +
            "   pv.user.id = :userId AND " +
            "   pv.post.id = :postId")
    Integer getPostVoteIdByUserIdAndPostId(
            @Param("userId") long userId,
            @Param("postId") long postId
    );
}
