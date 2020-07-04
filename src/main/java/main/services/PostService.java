package main.services;

import main.model.entities.Post;
import main.model.entities.User;
import main.model.enums.ActivityStatus;
import main.model.enums.ModerationStatus;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PostService {
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

    List<Integer> findAllYearsOfPublication(ActivityStatus activityStatus, ModerationStatus moderationStatus);

    Map<String, Long> getDateAndCountPosts(ActivityStatus activityStatus, ModerationStatus moderationStatus, int year);

    int getTotalCountOfNewPosts(ActivityStatus activityStatus);

    int getTotalCountOfPosts(ActivityStatus activityStatus, ModerationStatus moderationStatus);

    int getTotalCountOfPostsByDate(ActivityStatus activityStatus, ModerationStatus moderationStatus, String date);

    int getTotalCountOfPostsByModeratorId(ActivityStatus activityStatus, ModerationStatus moderationStatus, long moderatorId);

    int getTotalCountOfPostsByQuery(ActivityStatus activityStatus, ModerationStatus moderationStatus, String query);

    int getTotalCountOfPostsByTag(ActivityStatus activityStatus, ModerationStatus moderationStatus, String tag);

    int getTotalCountOfPostsByUserId(long userId);

    int getTotalCountOfPostsByUserId(ActivityStatus activityStatus, ModerationStatus moderationStatus, long userId);

    int getTotalCountOfHiddenPostsByUserId(long userId);

    int getTotalCountView(ActivityStatus activityStatus, ModerationStatus moderationStatus);

    int getTotalCountViewByUserId(long userId);

    Post findById(long postId);

    Post updateViewCount(Post post);

    LocalDateTime getDateOfTheEarliestPost(ActivityStatus activityStatus, ModerationStatus moderationStatus);

    LocalDateTime getDateOfTheEarliestPostByUserId(long userId);

    Post addPost(ActivityStatus activity, User user, LocalDateTime postTime, String postTitle, String postText);

    void setModerationStatus(long userId, long postId, ModerationStatus moderationStatus);
}
