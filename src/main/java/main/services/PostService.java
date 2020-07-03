package main.services;

import main.model.entities.Post;
import main.model.entities.User;
import main.model.enums.ActivesType;
import main.model.enums.ModerationStatus;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PostService {
    List<Post> findAllHiddenPostsByUserId(int offset, int limit, long userId);

    List<Post> findAllPostsByUserId(ActivesType activesType, ModerationStatus moderationStatus, int offset, int limit, long userId);

    List<Post> findAllPostBest(ActivesType activesType, ModerationStatus moderationStatus, int offset, int limit);

    List<Post> findAllPostByDate(ActivesType activesType, ModerationStatus moderationStatus, int offset, int limit, String date);

    List<Post> findAllPostByQuery(ActivesType activesType, ModerationStatus moderationStatus, int offset, int limit, String query);

    List<Post> findAllPostByTag(ActivesType activesType, ModerationStatus moderationStatus, int offset, int limit, String tag);

    List<Post> findAllPostPopular(ActivesType activesType, ModerationStatus moderationStatus, int offset, int limit);

    List<Post> findAllPostSortedByDate(ActivesType activesType, ModerationStatus moderationStatus, int offset, int limit, Sort.Direction direction);

    List<Post> findAllNewPosts(ActivesType activesType, int offset, int limit);

    List<Post> findAllPostsByModeratorId(ActivesType activesType, ModerationStatus moderationStatus, int offset, int limit, long moderatorId);

    List<Integer> findAllYearsOfPublication(ActivesType activesType, ModerationStatus moderationStatus);

    Map<String, Long> getDateAndCountPosts(ActivesType activesType, ModerationStatus moderationStatus, int year);

    int getTotalCountOfNewPosts(ActivesType activesType);

    int getTotalCountOfPosts(ActivesType activesType, ModerationStatus moderationStatus);

    int getTotalCountOfPostsByDate(ActivesType activesType, ModerationStatus moderationStatus, String date);

    int getTotalCountOfPostsByModeratorId(ActivesType activesType, ModerationStatus moderationStatus, long moderatorId);

    int getTotalCountOfPostsByQuery(ActivesType activesType, ModerationStatus moderationStatus, String query);

    int getTotalCountOfPostsByTag(ActivesType activesType, ModerationStatus moderationStatus, String tag);

    int getTotalCountOfPostsByUserId(long userId);

    int getTotalCountOfPostsByUserId(ActivesType activesType, ModerationStatus moderationStatus, long userId);

    int getTotalCountOfHiddenPostsByUserId(long userId);

    int getTotalCountView(ActivesType activesType, ModerationStatus moderationStatus);

    int getTotalCountViewByUserId(long userId);

    Post findById(long postId);

    Post updateViewCount(Post post);

    LocalDateTime getDateOfTheEarliestPost(ActivesType activesType, ModerationStatus moderationStatus);

    LocalDateTime getDateOfTheEarliestPostByUserId(long userId);

    Post addPost(byte isActive, User user, LocalDateTime postTime, String postTitle, String postText);

    void setModerationStatus(long userId, long postId, ModerationStatus moderationStatus);
}
