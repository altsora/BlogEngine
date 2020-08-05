package main.services;

import main.api.responses.ErrorsDTO;
import main.api.responses.PostPublicDTO;
import main.model.entity.Post;
import main.model.entity.User;
import main.model.enums.ActivityStatus;
import main.model.enums.ModerationStatus;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PostService {
    boolean postIsInvalid(String title, String text, ErrorsDTO errors);

    int getTotalCountOfNewPosts(ActivityStatus activityStatus);

    int getTotalCountOfHiddenPostsByUserId(long userId);

    int getTotalCountOfPosts(ActivityStatus activityStatus, ModerationStatus moderationStatus);

    int getTotalCountOfPostsByDate(ActivityStatus activityStatus, ModerationStatus moderationStatus, String date);

    int getTotalCountOfPostsByModeratorId(ActivityStatus activityStatus, ModerationStatus moderationStatus, long moderatorId);

    int getTotalCountOfPostsByTag(ActivityStatus activityStatus, ModerationStatus moderationStatus, String tag);

    int getTotalCountOfPostsByQuery(ActivityStatus activityStatus, ModerationStatus moderationStatus, String query);

    int getTotalCountOfPostsByUserId(long userId);

    int getTotalCountOfPostsByUserId(ActivityStatus activityStatus, ModerationStatus moderationStatus, long userId);

    int getTotalCountView(ActivityStatus activityStatus, ModerationStatus moderationStatus);

    int getTotalCountViewByUserId(long userId);

    void updatePost(long postId, User user, ActivityStatus activityStatus, LocalDateTime time, String title, String text);

    void setModerationStatus(long userId, long postId, ModerationStatus moderationStatus);

    List<Integer> findAllYearsOfPublication(ActivityStatus activityStatus, ModerationStatus moderationStatus);

    List<Post> findAllHiddenPostsByUserId(int offset, int limit, long userId);

    List<Post> findAllPostsByUserId(ActivityStatus activityStatus, ModerationStatus moderationStatus, int offset, int limit, long userId);

    List<Post> findAllPostBest(ActivityStatus activityStatus, ModerationStatus moderationStatus, int offset, int limit);

    List<Post> findAllPostByDate(ActivityStatus activityStatus, ModerationStatus moderationStatus, int offset, int limit, String date);

    List<Post> findAllPostByQuery(ActivityStatus activityStatus, ModerationStatus moderationStatus, int offset, int limit, String query);

    List<Post> findAllPostByTag(ActivityStatus activityStatus, ModerationStatus moderationStatus, int offset, int limit, String tag);

    List<Post> findAllPostPopular(ActivityStatus activityStatus, ModerationStatus moderationStatus, int offset, int limit);

    List<Post> findAllPostSortedByDate(ActivityStatus activityStatus, ModerationStatus moderationStatus, int offset, int limit, Sort.Direction direction);

    List<Post> findAllNewPosts(ActivityStatus activityStatus, int offset, int limit);

    List<Post> findAllPostsByModeratorId(ActivityStatus activityStatus, ModerationStatus moderationStatus, int offset, int limit, long moderatorId);

    List<PostPublicDTO> getPostsToDisplay(List<Post> postListRep);

    LocalDateTime getDateOfTheEarliestPost(ActivityStatus activityStatus, ModerationStatus moderationStatus);

    LocalDateTime getDateOfTheEarliestPostByUserId(long userId);

    Map<String, Long> getDateAndCountPosts(ActivityStatus activityStatus, ModerationStatus moderationStatus, int year);

    Post addPost(ActivityStatus activityStatus, User user, LocalDateTime postTime, String postTitle, String postText, boolean preModeration);

    Post findById(long postId);

    Post increaseViewCount(Post post);

    String getAnnounce(String text);
}
