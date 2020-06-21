package main.services;

import main.model.entities.Post;
import main.model.entities.User;
import main.model.enums.ActivesType;
import main.model.enums.ModerationStatusType;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PostService {
    List<Post> findAllHiddenPostsByUserId(int offset, int limit, long userId);

    List<Post> findAllPostsByUserId(ActivesType activesType, ModerationStatusType moderationStatusType, int offset, int limit, long userId);

    List<Post> findAllPostBest(ActivesType activesType, ModerationStatusType moderationStatusType, int offset, int limit);

    List<Post> findAllPostByDate(ActivesType activesType, ModerationStatusType moderationStatusType, int offset, int limit, String date);

    List<Post> findAllPostByQuery(ActivesType activesType, ModerationStatusType moderationStatusType, int offset, int limit, String query);

    List<Post> findAllPostByTag(ActivesType activesType, ModerationStatusType moderationStatusType, int offset, int limit, String tag);

    List<Post> findAllPostPopular(ActivesType activesType, ModerationStatusType moderationStatusType, int offset, int limit);

    List<Post> findAllPostSortedByDate(ActivesType activesType, ModerationStatusType moderationStatusType, int offset, int limit, Sort.Direction direction);

    List<Post> findAllNewPosts(ActivesType activesType, int offset, int limit);

    List<Post> findAllPostsByModeratorId(ActivesType activesType, ModerationStatusType moderationStatusType, int offset, int limit, long moderatorId);

    List<Integer> findAllYearsOfPublication(ActivesType activesType, ModerationStatusType moderationStatusType);

    Map<String, Long> getDateAndCountPosts(ActivesType activesType, ModerationStatusType moderationStatusType, int year);

    int getTotalCountOfNewPosts(ActivesType activesType);

    int getTotalCountOfPosts(ActivesType activesType, ModerationStatusType moderationStatusType);

    int getTotalCountOfPostsByDate(ActivesType activesType, ModerationStatusType moderationStatusType, String date);

    int getTotalCountOfPostsByModeratorId(ActivesType activesType, ModerationStatusType moderationStatusType, long moderatorId);

    int getTotalCountOfPostsByQuery(ActivesType activesType, ModerationStatusType moderationStatusType, String query);

    int getTotalCountOfPostsByTag(ActivesType activesType, ModerationStatusType moderationStatusType, String tag);

    int getTotalCountOfPostsByUserId(long userId);

    int getTotalCountOfPostsByUserId(ActivesType activesType, ModerationStatusType moderationStatusType, long userId);

    int getTotalCountOfHiddenPostsByUserId(long userId);

    int getTotalCountView(ActivesType activesType, ModerationStatusType moderationStatusType);

    int getTotalCountViewByUserId(long userId);

    Post findById(long postId);

    LocalDateTime getDateOfTheEarliestPost(ActivesType activesType, ModerationStatusType moderationStatusType);

    LocalDateTime getDateOfTheEarliestPostByUserId(long userId);

    Post addPost(byte isActive, User user, LocalDateTime postTime, String postTitle, String postText);

    void setModerationStatus(long userId, long postId, ModerationStatusType moderationStatusType);
}
