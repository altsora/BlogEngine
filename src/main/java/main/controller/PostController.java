package main.controller;

import lombok.RequiredArgsConstructor;
import main.model.entity.Post;
import main.model.entity.Tag;
import main.model.entity.User;
import main.model.enums.ActivityStatus;
import main.request.PostForm;
import main.response.PostFullDTO;
import main.response.PostPublicDTO;
import main.response.PublicPostsDTO;
import main.response.UserSimpleDTO;
import main.service.*;
import main.servlet.AuthorizeServlet;
import main.util.TimeUtil;
import org.json.simple.JSONObject;
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
import static main.util.MessageUtil.KEY_ERRORS;
import static main.util.MessageUtil.KEY_RESULT;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final AuthorizeServlet authorizeServlet;
    private final GlobalSettingsService globalSettingsService;
    private final PostService postService;
    private final PostVoteService postVoteService;
    private final PostCommentService postCommentService;
    private final TagService tagService;
    private final Tag2PostService tag2PostService;
    private final UserService userService;

    //==================================================================================================================

    @GetMapping(value = "/api/post")
    public ResponseEntity<PublicPostsDTO> getAllPosts(
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
        List<PostPublicDTO> posts = postService.getPostsToDisplay(postListRep);
        PublicPostsDTO response = PublicPostsDTO.builder().count(count).posts(posts).build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/api/post/search")
    public ResponseEntity<PublicPostsDTO> searchPost(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "query") String query
    ) {
        List<Post> postListRep = query.equals("") ?
                postService.findAllPostSortedByDate(ACTIVE, ACCEPTED, offset, limit, Sort.Direction.DESC) :
                postService.findAllPostByQuery(ACTIVE, ACCEPTED, offset, limit, query);
        List<PostPublicDTO> posts = postService.getPostsToDisplay(postListRep);
        int count = query.equals("") ?
                postService.getTotalCountOfPosts(ACTIVE, ACCEPTED) :
                postService.getTotalCountOfPostsByQuery(ACTIVE, ACCEPTED, query);
        PublicPostsDTO response = PublicPostsDTO.builder().count(count).posts(posts).build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/api/post/{id}")
    public ResponseEntity<PostFullDTO> getPostById(@PathVariable(value = "id") long id) {
        Post postRep = postService.findById(id);
        if (postRep == null) {
            return ResponseEntity.notFound().build();
        }
        if (authorizeServlet.isUserAuthorize()) {
            User user = userService.findById(authorizeServlet.getAuthorizedUserId());
            if (!user.isModerator() && user.getId() != postRep.getUser().getId()) {
                postRep = postService.updateViewCount(postRep);
            }
        } else {
            postRep = postService.updateViewCount(postRep);
        }

        long postId = postRep.getId();
        long userId = postRep.getUser().getId();
        String userName = postRep.getUser().getName();
        long timestamp = postRep.getTime().toInstant(TimeUtil.ZONE_OFFSET).getEpochSecond();

        PostFullDTO post = PostFullDTO.builder()
                .id(postId)
                .timestamp(timestamp)
                .active(postRep.getActivityStatus() == ACTIVE)
                .user(UserSimpleDTO.builder().id(userId).name(userName).build())
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

    @GetMapping(value = "/api/post/byDate")
    public ResponseEntity<PublicPostsDTO> getPostsByDate(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "date") String date
    ) {
        List<Post> postListRep = postService.findAllPostByDate(ACTIVE, ACCEPTED, offset, limit, date);
        List<PostPublicDTO> posts = postService.getPostsToDisplay(postListRep);
        int count = postService.getTotalCountOfPostsByDate(ACTIVE, ACCEPTED, date);

        PublicPostsDTO response = PublicPostsDTO.builder().count(count).posts(posts).build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/api/post/byTag")
    public ResponseEntity<PublicPostsDTO> getPostsByTag(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "tag") String tag
    ) {
        List<Post> postListRep = postService.findAllPostByTag(ACTIVE, ACCEPTED, offset, limit, tag);
        List<PostPublicDTO> posts = postService.getPostsToDisplay(postListRep);
        int count = postService.getTotalCountOfPostsByTag(ACTIVE, ACCEPTED, tag);
        PublicPostsDTO response = PublicPostsDTO.builder().count(count).posts(posts).build();
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/api/post/like")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> putLike(@RequestBody JSONObject request) {
        boolean result = false;
        if (authorizeServlet.isUserAuthorize()) {
            long userId = authorizeServlet.getAuthorizedUserId();
            long postId = (int) request.get("post_id");
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
        JSONObject response = new JSONObject();
        response.put(KEY_RESULT, result);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/api/post/dislike")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> putDislike(@RequestBody JSONObject request) {
        boolean result = false;
        if (authorizeServlet.isUserAuthorize()) {
            long userId = authorizeServlet.getAuthorizedUserId();
            long postId = (int) request.get("post_id");
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
        JSONObject response = new JSONObject();
        response.put(KEY_RESULT, result);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/api/post")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> addNewPost(@RequestBody PostForm postForm) {
        long postTimestamp = postForm.getTimestamp();
        int postActive = postForm.getActive();
        String postTitle = postForm.getTitle();
        List<String> postTags = postForm.getTags();
        String postTextWithHtml = postForm.getText();
        String postTextWithoutHtml = Jsoup.parse(postTextWithHtml).text();

        JSONObject errorResponse = new JSONObject();
        JSONObject errors = new JSONObject();
        if (postService.postIsInvalid(postTitle, postTextWithoutHtml, errors)) {
            errorResponse.put(KEY_RESULT, false);
            errorResponse.put(KEY_ERRORS, errors);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        LocalDateTime postTime = TimeUtil.getLocalDateTimeFromTimestamp(postTimestamp);
        TimeUtil.returnToPresentIfOld(postTime);

        ActivityStatus activity = postActive == 1 ? ACTIVE : INACTIVE;
        User user = userService.findById(authorizeServlet.getAuthorizedUserId());

        boolean preModerationIsEnabled = globalSettingsService.settingPostPreModerationIsEnabled();
        Post newPost = postService.addPost(activity, user, postTime, postTitle, postTextWithHtml, preModerationIsEnabled);

        for (String tagName : postTags) {
            Tag tag = tagService.createTagIfNoExistsAndReturn(tagName);
            tag2PostService.addTag2Post(newPost, tag);
        }

        JSONObject successResponse = new JSONObject();
        successResponse.put(KEY_RESULT, true);
        return ResponseEntity.ok(successResponse);
    }

    @PutMapping(value = "/api/post/{id}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> updatePost(
            @PathVariable(value = "id") long postId,
            @RequestBody PostForm postForm
    ) {
        long postTimestamp = postForm.getTimestamp();
        int newPostActivity = postForm.getActive();
        String newTitle = postForm.getTitle();
        List<String> newPostTags = postForm.getTags();
        String newTextWithHtml = postForm.getText();
        String newTextWithoutHtml = Jsoup.parse(newTextWithHtml).text();

        JSONObject errorResponse = new JSONObject();
        JSONObject errors = new JSONObject();
        if (postService.postIsInvalid(newTitle, newTextWithoutHtml, errors)) {
            errorResponse.put(KEY_RESULT, false);
            errorResponse.put(KEY_ERRORS, errors);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        LocalDateTime newTimeOfPost = TimeUtil.getLocalDateTimeFromTimestamp(postTimestamp);
        TimeUtil.returnToPresentIfOld(newTimeOfPost);

        ActivityStatus activityStatus = newPostActivity == 1 ? ACTIVE : INACTIVE;
        User user = userService.findById(authorizeServlet.getAuthorizedUserId());
        postService.updatePost(postId, user, activityStatus, newTimeOfPost, newTitle, newTextWithHtml);

        for (String tagName : newPostTags) {
            tagService.createTagIfNoExistsAndReturn(tagName);
        }
        tag2PostService.updateTagsByPostId(postId, newPostTags);

        JSONObject successResponse = new JSONObject();
        successResponse.put(KEY_RESULT, true);
        return ResponseEntity.ok(successResponse);
    }

    @GetMapping(value = "/api/post/moderation")
    public ResponseEntity<PublicPostsDTO> listOfPostsForModeration(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "status") String status
    ) {
        long userId = authorizeServlet.getAuthorizedUserId();
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
        List<PostPublicDTO> posts = postService.getPostsToDisplay(postListRep);
        PublicPostsDTO response = PublicPostsDTO.builder().count(count).posts(posts).build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/api/post/my")
    public ResponseEntity<PublicPostsDTO> getMyPosts(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "status") String status
    ) {
        long userId = authorizeServlet.getAuthorizedUserId();
        List<Post> postListRep = new ArrayList<>();
        int count = 0;
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
        List<PostPublicDTO> posts = postService.getPostsToDisplay(postListRep);
        PublicPostsDTO response = PublicPostsDTO.builder().count(count).posts(posts).build();
        return ResponseEntity.ok(response);
    }
}
