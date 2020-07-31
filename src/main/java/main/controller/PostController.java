package main.controller;

import lombok.RequiredArgsConstructor;
import main.model.entity.Post;
import main.model.entity.Tag;
import main.model.entity.User;
import main.model.enums.ActivityStatus;
import main.model.enums.Rating;
import main.request.PostForm;
import main.response.PostFullDTO;
import main.response.PostPublicDTO;
import main.response.PublicPostsDTO;
import main.response.UserSimpleDTO;
import main.service.*;
import main.servlet.AuthorizeServlet;
import main.util.TimeUtil;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static main.model.enums.ActivityStatus.ACTIVE;
import static main.model.enums.ActivityStatus.INACTIVE;
import static main.model.enums.ModerationStatus.*;
import static main.util.MessageUtil.*;
import static main.model.enums.Rating.*;

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
    @ResponseBody
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
        JSONObject response = new JSONObject();
        boolean result = false;
        if (globalSettingsService.settingMultiUserModeIsEnabled()) {
            long postTimestamp = postForm.getTimestamp();
            int postActive = postForm.getActive();
            String postTitle = postForm.getTitle();
            List<String> postTags = postForm.getTags();
            String postText = postForm.getText();

            JSONObject message = new JSONObject();
            if (postTitle.isEmpty()) {
                message.put(KEY_MESSAGE, TITLE_EMPTY);
                message.put(KEY_RESULT, false);
                return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
            }

            if (postText.isEmpty()) {
                message.put(KEY_MESSAGE, POST_EMPTY);
                message.put(KEY_RESULT, false);
                return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
            }

            if (postTitle.length() < 3) {
                message.put(KEY_MESSAGE, TITLE_SHORT);
                message.put(KEY_RESULT, false);
                return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
            }

            if (postText.length() < 50) {
                message.put(KEY_MESSAGE, POST_SHORT);
                message.put(KEY_RESULT, false);
                return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
            }

            LocalDateTime postTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(postTimestamp), TimeUtil.TIME_ZONE);

            if (postTime.isBefore(LocalDateTime.now(TimeUtil.TIME_ZONE))) {
                postTime = LocalDateTime.now(TimeUtil.TIME_ZONE);
            }

            ActivityStatus activity = postActive == 1 ? ACTIVE : INACTIVE;
            User user = userService.findById(authorizeServlet.getAuthorizedUserId());

            boolean moderation = globalSettingsService.settingPostPreModerationIsEnabled();
            Post newPost = postService.addPost(activity, user, postTime, postTitle, postText, moderation);

            for (String tagName : postTags) {
                Tag tag = tagService.createTagIfNoExistsAndReturn(tagName);
                tag2PostService.addTag2Post(newPost, tag);
            }

            result = true;
        }

        response.put(KEY_RESULT, result);
        return ResponseEntity.ok(response);
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
    @SuppressWarnings("unchecked")
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

    //TODO
//    @PutMapping(value = "/api/post/{id}")
//    @SuppressWarnings("unchecked")
//    public ResponseEntity<JSONObject> updatePost(
//            @PathVariable(value = "id") long postId,
//            @RequestBody PostForm postForm
//    ) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:s");
//
//        String postTimeString = postForm.getTime() + ":" + LocalDateTime.now(ZoneId.of("UTC")).getSecond(); // КОСТЫЛЬ
//        int newPostActivity = postForm.getActive();
//        String newTitle = postForm.getTitle();
//        List<String> newPostTags = postForm.getTags();
//        String newText = postForm.getText();
//
//        JSONObject message = new JSONObject();
//        if (newTitle.isEmpty()) {
//            message.put("message", "Заголовок не должен быть пустым!");
//            message.put("result", false);
//            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
//        }
//
//        if (newText.isEmpty()) {
//            message.put("message", "Пост не должен быть пустым!");
//            message.put("result", false);
//            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
//        }
//
//        if (newTitle.length() < 3) {
//            message.put("message", "Минимальное количество символов в заголовке - 3!");
//            message.put("result", false);
//            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
//        }
//
//        if (newText.length() < 50) {
//            message.put("message", "Минимальное количество символов в публикации - 50!");
//            message.put("result", false);
//            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
//        }
//
//        LocalDateTime newTimeOfPost;
//        try {
//            newTimeOfPost = LocalDateTime.parse(postTimeString, formatter);
//        } catch (DateTimeParseException e) {
//            message.put("message", "Необходимо указать дату!");
//            message.put("result", false);
//            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
//        }
//        if (newTimeOfPost.isBefore(LocalDateTime.now(ZoneId.of("UTC")))) {
//            newTimeOfPost = LocalDateTime.now(ZoneId.of("UTC"));
//        }
//
//        ActivityStatus activityStatus = newPostActivity == 1 ? ActivityStatus.ACTIVE : ActivityStatus.INACTIVE;
//        User user = userService.findById(authorizeServlet.getAuthorizedUserId());
//        postService.updatePost(postId, user, activityStatus, newTimeOfPost, newTitle, newText);
//
//        for (String tagName : newPostTags) {
//            tagService.createTagIfNoExistsAndReturn(tagName);
//        }
//        tag2PostService.updateTagsByPostId(postId, newPostTags);
//
//        JSONObject response = new JSONObject();
//        response.put("result", true);
//        return ResponseEntity.ok(response);
//    }
}
