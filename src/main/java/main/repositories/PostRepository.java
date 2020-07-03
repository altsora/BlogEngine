package main.repositories;

import main.model.enums.ModerationStatus;
import main.model.entities.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.Tuple;
import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    String COUNT_COMMENTS = "countComments";
    String COUNT_LIKES = "countLikes";
    String POST_TIME = "time";

    //=============================================================================

    /**
     * Запрос определяет общее количество постов.
     * Посты опубликованы до текущего момента времени,
     * удовлетворяют условиям активности и имеют определённый статус модерации.
     *
     * @param isActive             - указывает активность поста (активный или скрытый);
     * @param moderationStatus - указывает статус поста (новый, принят или отклонён);
     * @return Возвращает количество постов.
     */
    @Query("SELECT COUNT(p) FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   p.time <= now()")
    int getTotalCountOfPosts(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatus moderationStatus
    );

    /**
     * Запрос определяет общее количество постов конкретного пользователя.
     * Учитываются любые посты: активные/скрытые, прошлые/отложенные, новые/принятые/отклонённые.
     *
     * @param userId - ID пользователя;
     * @return - возвращает общее количество постов конкретного пользователя.
     */
    @Query("SELECT COUNT(p) FROM Post p " +
            "WHERE p.user.id = :userId")
    int getTotalCountOfPostsByUserId(@Param("userId") long userId);

    /**
     * Запрос определяет общее количество постов, в которых используется указанный тэг.
     * Посты опубликованы до текущего момента времени,
     * удовлетворяют условиям активности и имеют определённый статус модерации.
     *
     * @param isActive             - указывает активность поста (активный или скрытый);
     * @param moderationStatus - указывает статус поста (новый, принят или отклонён);
     * @param tag                  - тэг, по которому подсчитываются посты;
     * @return - возвращает общее количество постов с указанным тэгом.
     */
    @Query("SELECT COUNT(p) FROM Post p " +
            "LEFT JOIN Tag2Post tp ON p.id = tp.post.id " +
            "LEFT JOIN Tag t ON t.id = tp.tag.id " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   p.time <= now() AND " +
            "   t.name = :tag")
    int getTotalCountOfPostsByTag(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("tag") String tag
    );

    /**
     * Запрос определяет количество постов, опубликованных за указанную дату.
     * Посты удовлетворяют условиям активности и имеют определённый статус модерации.
     *
     * @param isActive             - указывает активность поста (активный или скрытый);
     * @param moderationStatus - указывает статус поста (новый, принят или отклонён);
     * @param year                 - год публикации поста. Целое число;
     * @param month                - месяц публикации поста. Целое число от 1 до 12;
     * @param dayOfMonth           - день публикации поста. Целое число от 1 до 28-31;
     * @return - возвращает общее количество постов за указанную дату.
     */
    @Query("SELECT COUNT(p) FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   YEAR(p.time) = :year AND " +
            "   MONTH(p.time) = :month AND " +
            "   DAYOFMONTH(p.time) = :dayOfMonth")
    int getTotalCountOfPostsByDate(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("year") int year,
            @Param("month") int month,
            @Param("dayOfMonth") int dayOfMonth
    );

    /**
     * Запрос возвращает общее количество постов, которые содержат в заголовке или в основном тексте указанную строку.
     * Посты опубликованы до текущего момента времени,
     * удовлетворяют условиям активности и имеют определённый статус модерации.
     *
     * @param isActive             - указывает активность поста (активный или скрытый);
     * @param moderationStatus - указывает статус поста (новый, принят или отклонён);
     * @param query                - строка, по которой осуществляется подсчёт постов;
     * @return - возвращает общее количество постов, содержащих указанную строку.
     */
    @Query("SELECT COUNT(p) FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   p.time <= now() AND " +
            "   p.title LIKE %:query% OR p.text LIKE %:query% ")
    int getTotalCountOfPostsByQuery(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("query") String query
    );

    /**
     * Запрос возвращает общее количество просмотров у всех постов.
     * Посты опубликованы до текущего момента времени,
     * удовлетворяют условиям активности и имеют определённый статус модерации.
     *
     * @param isActive             - указывает активность поста (активный или скрытый);
     * @param moderationStatus - указывает статус поста (новый, принят или отклонён);
     * @return - Возвращает общее число просмотров у всех постов.
     */
    @Query("SELECT SUM(p.viewCount) FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   p.time <= now()")
    int getTotalCountView(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatus moderationStatus
    );

    /**
     * Запрос возвращает общее количество просмотров постов конкретного пользователя.
     *
     * @param userId - ID пользователя;
     * @return - возвращает общее число просмотров постов пользователя.
     */
    @Query("SELECT COUNT(p.viewCount) FROM Post p " +
            "WHERE p.user.id = :userId")
    int getTotalCountViewByUserId(@Param("userId") long userId);

    @Query("SELECT COUNT(p) FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus")
    int getTotalCountOfNewPosts(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatus moderationStatus
    );

    @Query("SELECT COUNT(p) FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   p.moderator.id = :moderatorId")
    int getTotalCountOfPostsByModeratorId(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("moderatorId") long moderatorId
    );

    @Query("SELECT COUNT(p) FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.user.id = :userId")
    int getTotalCountOfHiddenPostsByUserId(
            @Param("isActive") byte isActive,
            @Param("userId") long userId
    );

    @Query("SELECT COUNT(p) FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   p.user.id = :userId")
    int getTotalCountOfPostsByUserId(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("userId") long userId
    );

    //=============================================================================

    /**
     * Запрос возвращает список дат за указанный год, сгрупированных по количеству постов в каждую из этих дат.
     * Посты опубликованы до текущего момента времени,
     * удовлетворяют условиям активности и имеют определённый статус модерации.
     *
     * @param isActive             - указывает активность поста (активный или скрытый);
     * @param moderationStatus - указывает статус поста (новый, принят или отклонён);
     * @param year                 - год, по которому осуществляется выборка;
     * @param sort                 - сортировка результирующей выборки;
     * @return Возвращает коллекцию пар значений: дата и количество постов в этот день.
     */
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
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("year") int year,
            Sort sort
    );

    /**
     * Запрос возвращает список, содержащий те года, в которые публиковался хотя бы один пост.
     * Посты опубликованы до текущего момента времени,
     * удовлетворяют условиям активности и имеют определённый статус модерации.
     *
     * @param isActive             - указывает активность поста (активный или скрытый);
     * @param moderationStatus - указывает статус поста (новый, принят или отклонён);
     * @param sort                 - сортировка результирующей выборки;
     * @return Возвращает коллекцию лет, в которые производились публикации.
     */
    @Query("SELECT YEAR(p.time) AS yearPost " +
            "FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   p.time <= now() " +
            "GROUP BY yearPost")
    List<Integer> findAllYearsOfPublication(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatus moderationStatus,
            Sort sort
    );

    //=============================================================================

    /**
     * Запрос возвращает все посты, отсортированные по дате.
     * Посты опубликованы до текущего момента времени,
     * удовлетворяют условиям активности и имеют определённый статус модерации.
     *
     * @param isActive             - указывает активность поста (активный или скрытый);
     * @param moderationStatus - указывает статус поста (новый, принят или отклонён);
     * @param pageable             - содержит в себе ограничение по количеству выдачи результатов, а также способ их сортировки;
     * @return Возвращает коллекцию постов, отсортированных по дате.
     */
    @Query("SELECT p FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   p.time <= now() ")
    List<Post> findAllPostSortedByDate(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatus moderationStatus,
            Pageable pageable
    );

    @Query("SELECT p FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   p.time <= now() ")
    List<Post> findAllRealPosts(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatus moderationStatus,
            Pageable pageable
    );

    @Query("SELECT p FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus")
    List<Post> findAllPosts(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatus moderationStatus,
            Pageable pageable
    );

    @Query("SELECT p FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   p.user.id = :userId")
    List<Post> findAllPostsByUserId(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("userId") long userId,
            Pageable pageable
    );

    @Query("SELECT p FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   p.moderator.id = :moderatorId")
    List<Post> findAllPostsByModeratorId(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("moderatorId") long moderatorId,
            Pageable pageable
    );

    /**
     * Запрос возвращает все посты, отсортированные по количеству просмотров.
     * Посты опубликованы до текущего момента времени,
     * удовлетворяют условиям активности и имеют определённый статус модерации.
     *
     * @param isActive             - указывает активность поста (активный или скрытый);
     * @param moderationStatus - указывает статус поста (новый, принят или отклонён);
     * @param pageable             - содержит в себе ограничение по количеству выдачи результатов, а также способ их сортировки;
     * @return Возвращает коллекцию постов, отсортированных по количеству просмотров.
     */
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
            @Param("moderationStatus") ModerationStatus moderationStatus,
            Pageable pageable
    );

    /**
     * Запрос возвращает все посты, отсортированные по лайкам.
     * Посты опубликованы до текущего момента времени,
     * удовлетворяют условиям активности и имеют определённый статус модерации.
     *
     * @param isActive             - указывает активность поста (активный или скрытый);
     * @param moderationStatus - указывает статус поста (новый, принят или отклонён);
     * @param pageable             - содержит в себе ограничение по количеству выдачи результатов, а также способ их сортировки;
     * @return Возвращает коллекцию постов, отсортированных по лайкам.
     */
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
            @Param("moderationStatus") ModerationStatus moderationStatus,
            Pageable pageable
    );

    //=============================================================================

    /**
     * Запрос возвращает посты, которые содержат в заголовке или в основном тексте указанную строку.
     * Посты опубликованы до текущего момента времени,
     * удовлетворяют условиям активности и имеют определённый статус модерации.
     *
     * @param isActive             - указывает активность поста (активный или скрытый);
     * @param moderationStatus - указывает статус поста (новый, принят или отклонён);
     * @param query                - строка, по которой осуществляется поиск постов;
     * @param pageable             - содержит в себе ограничение по количеству выдачи результатов, а также способ их сортировки.
     * @return
     */
    @Query("SELECT p FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   p.time <= now() AND " +
            "   p.title LIKE %:query% OR p.text LIKE %:query% ")
    List<Post> findAllPostByQuery(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("query") String query,
            Pageable pageable
    );

    //=============================================================================

    /**
     * Запрос возвращает пост по его идентификатору.
     * Посты опубликованы до текущего момента времени,
     * удовлетворяют условиям активности и имеют определённый статус модерации.
     *
     * @param postId               - ID поста;
     * @param isActive             - указывает активность поста (активный или скрытый);
     * @param moderationStatus - указывает статус поста (новый, принят или отклонён);
     * @return - возвращает пост по указанному идентификатору.
     */
    @Query("SELECT p FROM Post p " +
            "WHERE " +
            "   p.id = :postId AND" +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   p.time <= now()")
    Post findPostByPostId(
            @Param("postId") long postId,
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatus moderationStatus
    );

    //=============================================================================

    /**
     * Запрос возвращает все посты за указанный день.
     * Посты удовлетворяют условиям активности и имеют определённый статус модерации.
     *
     * @param isActive             - указывает активность поста (активный или скрытый);
     * @param moderationStatus - указывает статус поста (новый, принят или отклонён);
     * @param year                 - год публикации постов;
     * @param month                - месяц публикации постов;
     * @param dayOfMonth           - день публикации постов;
     * @param pageable             - содержит в себе ограничение по количеству выдачи результатов, а также способ их сортировки.
     * @return
     */
    @Query("SELECT p FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus AND " +
            "   YEAR(p.time) = :year AND " +
            "   MONTH(p.time) = :month AND " +
            "   DAYOFMONTH(p.time) = :dayOfMonth")
    List<Post> findAllPostByDate(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("year") int year,
            @Param("month") int month,
            @Param("dayOfMonth") int dayOfMonth,
            Pageable pageable
    );

    //=============================================================================

    /**
     * Запрос возвращает список постов, содержащих указанный тэг.
     * Посты опубликованы до текущего момента времени,
     * удовлетворяют условиям активности и имеют определённый статус модерации.
     *
     * @param isActive             - указывает активность поста (активный или скрытый);
     * @param moderationStatus - указывает статус поста (новый, принят или отклонён);
     * @param tag                  - тэг, по которому осуществляется поиск постов;
     * @param pageable             - содержит в себе ограничение по количеству выдачи результатов, а также способ их сортировки.
     * @return
     */
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
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("tag") String tag,
            Pageable pageable
    );

    @Query("SELECT p FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.user.id = :userId")
    List<Post> findAllHiddenPostsByUserId(
            @Param("isActive") byte isActive,
            @Param("userId") long userId,
            Pageable pageable
    );

    //=============================================================================

    /**
     * Запрос возвращает дату самого раннего поста.
     * Пост удовлетворяет условиям активности и имеет определённый статус модерации.
     *
     * @param isActive             - указывает активность поста (активный или скрытый);
     * @param moderationStatus - указывает статус поста (новый, принят или отклонён);
     * @return - возвращает дату самого раннего поста.
     */
    @Query("SELECT MIN(p.time) FROM Post p " +
            "WHERE " +
            "   p.isActive = :isActive AND " +
            "   p.moderationStatus = :moderationStatus"
    )
    LocalDateTime getDateOfTheEarliestPost(
            @Param("isActive") byte isActive,
            @Param("moderationStatus") ModerationStatus moderationStatus
    );

    /**
     * Запрос возвращает дату самого раннего поста указанного пользователя.
     * Если пользователь ничего не публиковал, возвращается null.
     *
     * @param userId - ID пользователя.
     * @return - воззвращает дату самого раннего поста указанного пользователя.
     */
    @Query("SELECT MIN(p.time) FROM Post p WHERE p.user.id = :userId")
    LocalDateTime getDateOfTheEarliestPostByUserId(@Param("userId") long userId);

}
