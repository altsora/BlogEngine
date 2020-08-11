package main.controller;

import lombok.RequiredArgsConstructor;
import main.api.requests.PostForm;
import main.api.requests.RatingForm;
import main.api.responses.*;
import main.model.entities.Post;
import main.model.entities.Tag;
import main.model.entities.User;
import main.model.enums.ActivityStatus;
import main.services.*;
import main.utils.TimeUtil;
import org.jsoup.Jsoup;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static main.model.enums.ActivityStatus.ACTIVE;
import static main.model.enums.ActivityStatus.INACTIVE;
import static main.model.enums.ModerationStatus.*;
import static main.model.enums.Rating.DISLIKE;
import static main.model.enums.Rating.LIKE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {
    private final AuthService authService;
    private final GlobalSettingsService globalSettingsService;
    private final PostService postService;
    private final PostVoteService postVoteService;
    private final PostCommentService postCommentService;
    private final TagService tagService;
    private final Tag2PostService tag2PostService;
    private final UserService userService;

    //==================================================================================================================

    @GetMapping
    public ResponseEntity<AbstractResponse> getAllPosts(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "mode") String mode
    ) {
        List<Post> postListRep = new ArrayList<>();
        int count = postService.getTotalCountOfPosts(ACTIVE, ACCEPTED);
        switch (mode) {
            case "popular":
                postListRep = postService.findAllPostPopular(ACTIVE, ACCEPTED, offset, limit);
                break;
            case "best":
                postListRep = postService.findAllPostBest(ACTIVE, ACCEPTED, offset, limit);
                break;
            case "early":
                postListRep = postService.findAllPostSortedByDate(ACTIVE, ACCEPTED, offset, limit, Sort.Direction.ASC);
                break;
            case "recent":
                postListRep = postService.findAllPostSortedByDate(ACTIVE, ACCEPTED, offset, limit, Sort.Direction.DESC);
                break;
        }
        List<PostResponse> posts = postService.getPostsToDisplay(postListRep);
        PublicPostsResponse response = PublicPostsResponse.builder().count(count).posts(posts).build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<AbstractResponse> searchPost(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "query") String query
    ) {
        List<Post> postListRep = query.equals("") ?
                postService.findAllPostSortedByDate(ACTIVE, ACCEPTED, offset, limit, Sort.Direction.DESC) :
                postService.findAllPostByQuery(ACTIVE, ACCEPTED, offset, limit, query);
        List<PostResponse> posts = postService.getPostsToDisplay(postListRep);
        int count = query.equals("") ?
                postService.getTotalCountOfPosts(ACTIVE, ACCEPTED) :
                postService.getTotalCountOfPostsByQuery(ACTIVE, ACCEPTED, query);
        PublicPostsResponse response = PublicPostsResponse.builder().count(count).posts(posts).build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AbstractResponse> getPostById(@PathVariable(value = "id") long id) {
        Post postRep = postService.findById(id);
        if (postRep == null) {
            return ResponseEntity.notFound().build();
        }
        if (authService.isUserAuthorize()) {
            User user = userService.findById(authService.getAuthorizedUserId());
            if (!user.isModerator() && user.getId() != postRep.getUser().getId()) {
                postRep = postService.increaseViewCount(postRep);
            }
        } else {
            postRep = postService.increaseViewCount(postRep);
        }

        long postId = postRep.getId();
        long userId = postRep.getUser().getId();
        String userName = postRep.getUser().getName();
        long timestamp = TimeUtil.getTimestampFromLocalDateTime(postRep.getTime());

        PostFullResponse post = PostFullResponse.builder()
                .id(postId)
                .timestamp(timestamp)
                .active(postRep.getActivityStatus() == ACTIVE)
                .user(UserSimpleResponse.builder().id(userId).name(userName).build())
                .title(postRep.getTitle())
                .text(postRep.getText())
                .likeCount(postVoteService.getCountLikesByPostId(postId))
                .dislikeCount(postVoteService.getCountDislikesByPostId(postId))
                .viewCount(postRep.getViewCount())
                .comments(postCommentService.getCommentsByPostId(postId))
                .tags(tagService.getTagsByPostId(postId))
                .build();
        return ResponseEntity.ok(post);
    }

    @GetMapping("/byDate")
    public ResponseEntity<AbstractResponse> getPostsByDate(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "date") String date
    ) {
        List<Post> postListRep = postService.findAllPostByDate(ACTIVE, ACCEPTED, offset, limit, date);
        List<PostResponse> posts = postService.getPostsToDisplay(postListRep);
        int count = postService.getTotalCountOfPostsByDate(ACTIVE, ACCEPTED, date);
        PublicPostsResponse response = PublicPostsResponse.builder().count(count).posts(posts).build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/byTag")
    public ResponseEntity<AbstractResponse> getPostsByTag(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "tag") String tag
    ) {
        List<Post> postListRep = postService.findAllPostByTag(ACTIVE, ACCEPTED, offset, limit, tag);
        List<PostResponse> posts = postService.getPostsToDisplay(postListRep);
        int count = postService.getTotalCountOfPostsByTag(ACTIVE, ACCEPTED, tag);
        PublicPostsResponse response = PublicPostsResponse.builder().count(count).posts(posts).build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/like")
    public ResponseEntity<AbstractResponse> putLike(@RequestBody RatingForm ratingForm) {
        boolean result = false;
        if (authService.isUserAuthorize()) {
            long userId = authService.getAuthorizedUserId();
            long postId = ratingForm.getPostId();
            if (postVoteService.userDislikeAlreadyExists(userId, postId)) {
                long postVoteId = postVoteService.getIdByUserIdAndPostId(userId, postId);
                postVoteService.replaceValue(postVoteId);
                result = true;
            } else if (postVoteService.userLikeAlreadyExists(userId, postId)) {
                long postVoteId = postVoteService.getIdByUserIdAndPostId(userId, postId);
                postVoteService.deleteById(postVoteId);
                result = false;
            } else {
                postVoteService.setRating(userId, postId, LIKE);
                result = true;
            }
        }
        return ResponseEntity.ok(new ResultResponse(result));
    }

    @PostMapping("/dislike")
    public ResponseEntity<AbstractResponse> putDislike(@RequestBody RatingForm ratingForm) {
        boolean result = false;
        if (authService.isUserAuthorize()) {
            long userId = authService.getAuthorizedUserId();
            long postId = ratingForm.getPostId();
            if (postVoteService.userLikeAlreadyExists(userId, postId)) {
                long postVoteId = postVoteService.getIdByUserIdAndPostId(userId, postId);
                postVoteService.replaceValue(postVoteId);
                result = true;
            } else if (postVoteService.userDislikeAlreadyExists(userId, postId)) {
                long postVoteId = postVoteService.getIdByUserIdAndPostId(userId, postId);
                postVoteService.deleteById(postVoteId);
                result = false;
            } else {
                postVoteService.setRating(userId, postId, DISLIKE);
                result = true;
            }
        }
        return ResponseEntity.ok(new ResultResponse(result));
    }

    @PostMapping
    public ResponseEntity<AbstractResponse> addNewPost(@RequestBody PostForm postForm) {
        long postTimestamp = postForm.getTimestamp();
        int postActive = postForm.getActive();
        String postTitle = postForm.getTitle();
        List<String> postTags = postForm.getTags();
        String postTextWithHtml = postForm.getText();
        String postTextWithoutHtml = Jsoup.parse(postTextWithHtml).text();

        ErrorResponse errors = new ErrorResponse();
        if (postService.postIsInvalid(postTitle, postTextWithoutHtml, errors)) {
            ResultResponse errorResponse = new ResultResponse(errors);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        LocalDateTime postTime = TimeUtil.getLocalDateTimeFromTimestamp(postTimestamp);
        TimeUtil.returnToPresentIfOld(postTime);
        ActivityStatus activity = postActive == 1 ? ACTIVE : INACTIVE;
        User user = userService.findById(authService.getAuthorizedUserId());
        boolean preModerationIsEnabled = globalSettingsService.settingPostPreModerationIsEnabled();
        Post newPost = postService.addPost(activity, user, postTime, postTitle, postTextWithHtml, preModerationIsEnabled);

        for (String tagName : postTags) {
            Tag tag = tagService.createTagIfNoExistsAndReturn(tagName);
            tag2PostService.addTag2Post(newPost, tag);
        }

        ResultResponse successResponse = new ResultResponse(true);
        return ResponseEntity.ok(successResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AbstractResponse> updatePost(
            @PathVariable(value = "id") long postId,
            @RequestBody PostForm postForm
    ) {
        long postTimestamp = postForm.getTimestamp();
        int newPostActivity = postForm.getActive();
        String newTitle = postForm.getTitle();
        List<String> newPostTags = postForm.getTags();
        String newTextWithHtml = postForm.getText();
        String newTextWithoutHtml = Jsoup.parse(newTextWithHtml).text();

        ErrorResponse errors = new ErrorResponse();
        if (postService.postIsInvalid(newTitle, newTextWithoutHtml, errors)) {
            ResultResponse errorResponse = new ResultResponse(errors);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        LocalDateTime newTimeOfPost = TimeUtil.getLocalDateTimeFromTimestamp(postTimestamp);
        TimeUtil.returnToPresentIfOld(newTimeOfPost);
        ActivityStatus activityStatus = newPostActivity == 1 ? ACTIVE : INACTIVE;
        User user = userService.findById(authService.getAuthorizedUserId());
        postService.updatePost(postId, user, activityStatus, newTimeOfPost, newTitle, newTextWithHtml);

        for (String tagName : newPostTags) {
            tagService.createTagIfNoExistsAndReturn(tagName);
        }
        tag2PostService.updateTagsByPostId(postId, newPostTags);

        ResultResponse successResponse = new ResultResponse(true);
        return ResponseEntity.ok(successResponse);
    }

    @GetMapping("/moderation")
    public ResponseEntity<AbstractResponse> listOfPostsForModeration(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "status") String status
    ) {
        long userId = authService.getAuthorizedUserId();
        int count = 0;
        List<Post> postListRep = new ArrayList<>();
        switch (status) {
            case "declined":
                postListRep = postService.findAllPostsByModeratorId(ACTIVE, DECLINED, offset, limit, userId);
                count = postService.getTotalCountOfPostsByModeratorId(ACTIVE, DECLINED, userId);
                break;
            case "accepted":
                postListRep = postService.findAllPostsByModeratorId(ACTIVE, ACCEPTED, offset, limit, userId);
                count = postService.getTotalCountOfPostsByModeratorId(ACTIVE, ACCEPTED, userId);
                break;
            case "new":
                postListRep = postService.findAllNewPosts(ACTIVE, offset, limit);
                count = postService.getTotalCountOfNewPosts(ACTIVE);
                break;
        }
        List<PostResponse> posts = postService.getPostsToDisplay(postListRep);
        PublicPostsResponse response = PublicPostsResponse.builder().count(count).posts(posts).build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<AbstractResponse> getMyPosts(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "status") String status
    ) {
        long userId = authService.getAuthorizedUserId();
        int count = 0;
        List<Post> postListRep = new ArrayList<>();
        switch (status) {
            case "inactive":
                postListRep = postService.findAllHiddenPostsByUserId(offset, limit, userId);
                count = postService.getTotalCountOfHiddenPostsByUserId(userId);
                break;
            case "pending":
                postListRep = postService.findAllPostsByUserId(ACTIVE, NEW, offset, limit, userId);
                count = postService.getTotalCountOfPostsByUserId(ACTIVE, NEW, userId);
                break;
            case "declined":
                postListRep = postService.findAllPostsByUserId(ACTIVE, DECLINED, offset, limit, userId);
                count = postService.getTotalCountOfPostsByUserId(ACTIVE, DECLINED, userId);
                break;
            case "published":
                postListRep = postService.findAllPostsByUserId(ACTIVE, ACCEPTED, offset, limit, userId);
                count = postService.getTotalCountOfPostsByUserId(ACTIVE, ACCEPTED, userId);
                break;
        }
        List<PostResponse> posts = postService.getPostsToDisplay(postListRep);
        PublicPostsResponse response = PublicPostsResponse.builder().count(count).posts(posts).build();
        return ResponseEntity.ok(response);
    }
}
