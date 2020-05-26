package main.services;

import main.model.entities.Post;
import main.model.entities.PostVote;
import main.model.entities.enums.ActivesType;
import main.model.entities.enums.ModerationStatusType;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PostService {
    List<Post> findAllPostBest(ActivesType activesType, ModerationStatusType moderationStatusType, int offset, int limit);

    List<Post> findAllPostByDate(ActivesType activesType, ModerationStatusType moderationStatusType, int offset, int limit, String date);

    List<Post> findAllPostByQuery(ActivesType activesType, ModerationStatusType moderationStatusType, int offset, int limit, String query);

    List<Post> findAllPostByTag(ActivesType activesType, ModerationStatusType moderationStatusType, int offset, int limit, String tag);

    List<Post> findAllPostPopular(ActivesType activesType, ModerationStatusType moderationStatusType, int offset, int limit);

    List<Post> findAllPostSortedByDate(ActivesType activesType, ModerationStatusType moderationStatusType, int offset, int limit, Sort.Direction direction);

    List<Integer> findAllYearsOfPublication(ActivesType activesType, ModerationStatusType moderationStatusType);

    Map<String, Long> getDateAndCountPosts(ActivesType activesType, ModerationStatusType moderationStatusType, int year);

    int getTotalCountOfPosts(ActivesType activesType, ModerationStatusType moderationStatusType);

    int getTotalCountOfPostsByDate(ActivesType activesType, ModerationStatusType moderationStatusType, String date);

    int getTotalCountOfPostsByQuery(ActivesType activesType, ModerationStatusType moderationStatusType, String query);

    int getTotalCountOfPostsByTag(ActivesType activesType, ModerationStatusType moderationStatusType, String tag);

    int getTotalCountOfPostsByUserId(long userId);

    int getTotalCountView(ActivesType activesType, ModerationStatusType moderationStatusType);

    int getTotalCountViewByUserId(long userId);

    Post findById(long postId);

    Post findPostByIdWithCondition(long postId, ActivesType activesType, ModerationStatusType moderationStatusType);

    LocalDateTime getDateOfTheEarliestPost(ActivesType activesType, ModerationStatusType moderationStatusType);

    LocalDateTime getDateOfTheEarliestPostByUserId(long userId);

    Post addPostAndReturn(Post post);
}
