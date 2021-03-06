package main.services.impl;

import lombok.RequiredArgsConstructor;
import main.api.responses.ErrorResponse;
import main.api.responses.PostResponse;
import main.api.responses.UserSimpleResponse;
import main.model.entities.Post;
import main.model.entities.User;
import main.model.enums.ActivityStatus;
import main.model.enums.ModerationStatus;
import main.repositories.PostRepository;
import main.services.PostCommentService;
import main.services.PostService;
import main.services.PostVoteService;
import main.services.UserService;
import main.utils.TimeUtil;
import org.jsoup.Jsoup;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.model.enums.ActivityStatus.INACTIVE;
import static main.model.enums.ModerationStatus.ACCEPTED;
import static main.model.enums.ModerationStatus.NEW;
import static main.model.enums.Rating.LIKE;
import static main.utils.MessageUtil.*;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostCommentService postCommentService;
    private final PostRepository postRepository;
    private final PostVoteService postVoteService;
    private final UserService userService;

    //==================================================================================================================

    @Override
    public List<Post> findAllPostPopular(ActivityStatus activityStatus, ModerationStatus moderationStatus, int offset, int limit) {
        int pageNumber = offset / limit;
        Pageable sortedByCountComment = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.DESC, PostRepository.COUNT_COMMENTS));
        return postRepository.findAllPostPopular(activityStatus, moderationStatus, sortedByCountComment);
    }

    @Override
    public List<Post> findAllHiddenPostsByUserId(int offset, int limit, long userId) {
        int pageNumber = offset / limit;
        Pageable sortedByPostTime = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.DESC, PostRepository.POST_TIME));
        return postRepository.findAllHiddenPostsByUserId(INACTIVE, userId, sortedByPostTime);
    }

    @Override
    public List<Post> findAllPostsByUserId(ActivityStatus activityStatus, ModerationStatus moderationStatus, int offset, int limit, long userId) {
        int pageNumber = offset / limit;
        Pageable sortedByPostTime = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.DESC, PostRepository.POST_TIME));
        return postRepository.findAllPostsByUserId(activityStatus, moderationStatus, userId, sortedByPostTime);
    }

    @Override
    public List<Post> findAllPostBest(ActivityStatus activityStatus, ModerationStatus moderationStatus, int offset, int limit) {
        int pageNumber = offset / limit;
        Pageable sortedByCountLikes = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.DESC, PostRepository.COUNT_LIKES));
        return postRepository.findAllPostBest(activityStatus, moderationStatus, LIKE, sortedByCountLikes);
    }

    @Override
    public List<Post> findAllPostSortedByDate(ActivityStatus activityStatus, ModerationStatus moderationStatus, int offset, int limit, Sort.Direction direction) {
        int pageNumber = offset / limit;
        Pageable sortedByPostTime = PageRequest.of(pageNumber, limit, Sort.by(direction, PostRepository.POST_TIME));
        return postRepository.findAllPostSortedByDate(activityStatus, moderationStatus, sortedByPostTime);
    }

    @Override
    public List<Post> findAllNewPosts(ActivityStatus activityStatus, int offset, int limit) {
        int pageNumber = offset / limit;
        Pageable sortedByPostTime = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.DESC, PostRepository.POST_TIME));
        return postRepository.findAllPosts(activityStatus, NEW, sortedByPostTime);
    }

    @Override
    public List<Post> findAllPostsByModeratorId(ActivityStatus activityStatus, ModerationStatus moderationStatus, int offset, int limit, long moderatorId) {
        int pageNumber = offset / limit;
        Pageable sortedByPostTime = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.DESC, PostRepository.POST_TIME));
        return postRepository.findAllPostsByModeratorId(activityStatus, moderationStatus, moderatorId, sortedByPostTime);
    }

    @Override
    public List<Post> findAllPostByQuery(ActivityStatus activityStatus, ModerationStatus moderationStatus, int offset, int limit, String query) {
        int pageNumber = offset / limit;
        Pageable sortedByPostTime = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.ASC, PostRepository.POST_TIME));
        return postRepository.findAllPostByQuery(activityStatus, moderationStatus, query, sortedByPostTime);
    }

    @Override
    public List<Post> findAllPostByDate(ActivityStatus activityStatus, ModerationStatus moderationStatus, int offset, int limit, String date) {
        int pageNumber = offset / limit;
        String[] var = date.split("-");
        int year = Integer.parseInt(var[0]);
        int month = Integer.parseInt(var[1]);
        int dayOfMonth = Integer.parseInt(var[2]);
        Pageable sortedByPostTime = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.ASC, PostRepository.POST_TIME));
        return postRepository.findAllPostByDate(activityStatus, moderationStatus, year, month, dayOfMonth, sortedByPostTime);
    }

    @Override
    public List<Post> findAllPostByTag(ActivityStatus activityStatus, ModerationStatus moderationStatus, int offset, int limit, String tag) {
        int pageNumber = offset / limit;
        Pageable sortedByPostTime = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.ASC, PostRepository.POST_TIME));
        return postRepository.findAllPostByTag(activityStatus, moderationStatus, tag, sortedByPostTime);
    }

    @Override
    public List<Integer> findAllYearsOfPublication(ActivityStatus activityStatus, ModerationStatus moderationStatus) {
        Sort sort = Sort.by(Sort.Direction.DESC, PostRepository.POST_TIME);
        return postRepository.findAllYearsOfPublication(activityStatus, moderationStatus, sort);
    }

    @Override
    public Map<String, Long> getDateAndCountPosts(ActivityStatus activityStatus, ModerationStatus moderationStatus, int year) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd");
        Map<String, Long> result = new HashMap<>();
        List<Tuple> datesAndCountPosts = postRepository
                .getDateAndCountPosts(activityStatus, moderationStatus, year, Sort.by(Sort.Direction.ASC, PostRepository.POST_TIME));
        for (Tuple tuple : datesAndCountPosts) {
            Object[] pair = tuple.toArray();
            try {
                LocalDateTime localTime = (LocalDateTime) pair[0];
                LocalDateTime utcTime = TimeUtil.convertLocalTimeInUtcTime(localTime);
                String date = formatter.format(utcTime);
                Long count = (Long) pair[1];
                result.put(date, count);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public int getTotalCountOfNewPosts(ActivityStatus activityStatus) {
        return postRepository.getTotalCountOfNewPosts(activityStatus, NEW);
    }

    @Override
    public int getTotalCountOfPostsByDate(ActivityStatus activityStatus, ModerationStatus moderationStatus, String date) {
        String[] var = date.split("-");
        int year = Integer.parseInt(var[0]);
        int month = Integer.parseInt(var[1]);
        int dayOfMonth = Integer.parseInt(var[2]);
        return postRepository.getTotalCountOfPostsByDate(activityStatus, moderationStatus, year, month, dayOfMonth);
    }

    @Override
    public int getTotalCountOfPostsByModeratorId(ActivityStatus activityStatus, ModerationStatus moderationStatus, long moderatorId) {
        return postRepository.getTotalCountOfPostsByModeratorId(activityStatus, moderationStatus, moderatorId);
    }

    @Override
    public int getTotalCountView(ActivityStatus activityStatus, ModerationStatus moderationStatus) {
        return postRepository.getTotalCountView(activityStatus, moderationStatus);
    }

    @Override
    public int getTotalCountViewByUserId(long userId) {
        return postRepository.getTotalCountViewByUserId(userId);
    }

    @Override
    public int getTotalCountOfPostsByUserId(long userId) {
        return postRepository.getTotalCountOfPostsByUserId(userId);
    }

    @Override
    public int getTotalCountOfPostsByUserId(ActivityStatus activityStatus, ModerationStatus moderationStatus, long userId) {
        return postRepository.getTotalCountOfPostsByUserId(activityStatus, moderationStatus, userId);
    }

    @Override
    public int getTotalCountOfHiddenPostsByUserId(long userId) {
        return postRepository.getTotalCountOfHiddenPostsByUserId(INACTIVE, userId);
    }

    @Override
    public int getTotalCountOfPosts(ActivityStatus activityStatus, ModerationStatus moderationStatus) {
        return postRepository.getTotalCountOfPosts(activityStatus, moderationStatus);
    }

    @Override
    public int getTotalCountOfPostsByQuery(ActivityStatus activityStatus, ModerationStatus moderationStatus, String query) {
        return postRepository.getTotalCountOfPostsByQuery(activityStatus, moderationStatus, query);
    }

    @Override
    public int getTotalCountOfPostsByTag(ActivityStatus activityStatus, ModerationStatus moderationStatus, String tag) {
        return postRepository.getTotalCountOfPostsByTag(activityStatus, moderationStatus, tag);
    }

    @Override
    public Post findById(long postId) {
        return postRepository.findById(postId).orElse(null);
    }

    @Override
    public Post increaseViewCount(Post post) {
        post.setViewCount(post.getViewCount() + 1);
        return postRepository.saveAndFlush(post);
    }

    @Override
    public void updatePost(long postId, User user, ActivityStatus activityStatus, LocalDateTime time, String title, String text) {
        Post updatedPost = findById(postId);
        if (user.isModerator()) {
            updatedPost.setModerator(user);
        } else {
            updatedPost.setModerationStatus(NEW);
        }
        updatedPost.setActivityStatus(activityStatus);
        updatedPost.setTime(time);
        updatedPost.setTitle(title);
        updatedPost.setText(text);
        postRepository.saveAndFlush(updatedPost);
    }

    @Override
    public LocalDateTime getDateOfTheEarliestPost(ActivityStatus activityStatus, ModerationStatus moderationStatus) {
        LocalDateTime dateTime = postRepository.getDateOfTheEarliestPost(activityStatus, moderationStatus);
        if (dateTime != null) {
            dateTime = TimeUtil.convertLocalTimeInUtcTime(dateTime);
        }
        return dateTime;
    }

    @Override
    public LocalDateTime getDateOfTheEarliestPostByUserId(long userId) {
        LocalDateTime dateTime = postRepository.getDateOfTheEarliestPostByUserId(userId);
        if (dateTime != null) {
            dateTime = TimeUtil.convertLocalTimeInUtcTime(dateTime);
        }
        return dateTime;
    }

    @Override
    public Post addPost(ActivityStatus activityStatus, User user, LocalDateTime postTime, String postTitle, String postText, boolean preModerationIsEnabled) {
        Post post = new Post();
        post.setActivityStatus(activityStatus);
        post.setUser(user);
        post.setTime(postTime);
        post.setTitle(postTitle);
        post.setText(postText);
        if (!preModerationIsEnabled) {
            post.setModerationStatus(ACCEPTED);
        }
        return postRepository.saveAndFlush(post);
    }

    @Override
    public void setModerationStatus(long userId, long postId, ModerationStatus moderationStatus) {
        User moderator = userService.findById(userId);
        Post post = postRepository.findById(postId).orElseThrow();
        post.setModerationStatus(moderationStatus);
        post.setModerator(moderator);
        postRepository.saveAndFlush(post);
    }

    @Override
    public String getAnnounce(String text) {
        int maxSizeAnnounce = 200;
        String announce = Jsoup.parse(text).text();
        if (announce.length() > maxSizeAnnounce) {
            announce = announce.substring(0, maxSizeAnnounce);
        }
        return announce;
    }

    @Override
    public List<PostResponse> getPostsToDisplay(List<Post> postListRep) {
        List<PostResponse> posts = new ArrayList<>();
        for (Post postRep : postListRep) {
            long postId = postRep.getId();
            long userId = postRep.getUser().getId();
            String userName = postRep.getUser().getName();
            long timestamp = TimeUtil.getTimestampFromLocalDateTime(postRep.getTime());

            PostResponse postResponse = PostResponse.builder()
                    .id(postId)
                    .timestamp(timestamp)
                    .title(postRep.getTitle())
                    .announce(getAnnounce(postRep.getText()))
                    .user(UserSimpleResponse.builder().id(userId).name(userName).build())
                    .likeCount(postVoteService.getCountLikesByPostId(postId))
                    .dislikeCount(postVoteService.getCountDislikesByPostId(postId))
                    .commentCount(postCommentService.getCountCommentsByPostId(postId))
                    .viewCount(postRep.getViewCount())
                    .build();
            posts.add(postResponse);
        }
        return posts;
    }

    @Override
    public boolean postIsInvalid(String title, String text, ErrorResponse errors) {
        int minTitleLength = 3;
        int minTextLength = 50;
        boolean result = false;

        if (title.isEmpty()) {
            result = true;
            errors.setTitle(MESSAGE_TITLE_EMPTY);
        } else if (title.length() < minTitleLength) {
            result = true;
            errors.setTitle(MESSAGE_TITLE_SHORT);
        }

        if (text.isEmpty()) {
            result = true;
            errors.setText(MESSAGE_POST_EMPTY);
        } else if (text.length() < minTextLength) {
            result = true;
            errors.setText(MESSAGE_POST_SHORT);
        }

        return result;
    }
}
