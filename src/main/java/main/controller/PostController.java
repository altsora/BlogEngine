package main.controller;

import lombok.RequiredArgsConstructor;
import main.model.entities.*;
import main.model.enums.ActivityStatus;
import main.model.enums.ModerationStatus;
import main.model.enums.Rating;
import main.responses.*;
import main.services.*;
import main.servlet.AuthorizeServlet;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

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
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> getAllPosts(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "mode") String mode
    ) {
        List<Post> postListRep;
        switch (mode) {
            case "popular":
                postListRep = postService.findAllPostPopular(ActivityStatus.ACTIVE, ModerationStatus.ACCEPTED, offset, limit);
                break;
            case "best":
                postListRep = postService.findAllPostBest(ActivityStatus.ACTIVE, ModerationStatus.ACCEPTED, offset, limit);
                break;
            case "early":
                postListRep = postService.findAllPostSortedByDate(ActivityStatus.ACTIVE, ModerationStatus.ACCEPTED, offset, limit, Sort.Direction.ASC);
                break;
            default:
                postListRep = postService.findAllPostSortedByDate(ActivityStatus.ACTIVE, ModerationStatus.ACCEPTED, offset, limit, Sort.Direction.DESC);
        }
        List<PostPublicDTO> posts = getPosts(postListRep);
        int count = postService.getTotalCountOfPosts(ActivityStatus.ACTIVE, ModerationStatus.ACCEPTED);
        JSONObject response = new JSONObject();
        response.put("count", count);
        response.put("posts", posts);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/api/post/search")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> searchPost(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "query") String query
    ) {
        List<Post> postListRep = query.equals("") ?
                postService.findAllPostSortedByDate(ActivityStatus.ACTIVE, ModerationStatus.ACCEPTED, offset, limit, Sort.Direction.DESC) :
                postService.findAllPostByQuery(ActivityStatus.ACTIVE, ModerationStatus.ACCEPTED, offset, limit, query);
        List<PostPublicDTO> posts = getPosts(postListRep);
        int count = query.equals("") ?
                postService.getTotalCountOfPosts(ActivityStatus.ACTIVE, ModerationStatus.ACCEPTED) :
                postService.getTotalCountOfPostsByQuery(ActivityStatus.ACTIVE, ModerationStatus.ACCEPTED, query);
        JSONObject response = new JSONObject();
        response.put("count", count);
        response.put("posts", posts);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/api/post/{id}")
    @ResponseBody
    public ResponseEntity<PostFullDTO> getPostById(@PathVariable(value = "id") long id) {
        Post postRep = postService.findById(id);
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

        PostFullDTO post = PostFullDTO.builder()
                .id(postId)
                .time(getStringTime(postRep.getTime()))
                .active(postRep.getActivityStatus() == ActivityStatus.ACTIVE)
                .user(UserSimpleDTO.builder().id(userId).name(userName).build())
                .title(postRep.getTitle())
                .text(postRep.getText())
                .likeCount(postVoteService.getCountLikesByPostId(postId))
                .dislikeCount(postVoteService.getCountDislikesByPostId(postId))
                .viewCount(postRep.getViewCount())
                .comments(getCommentsByPostId(postId))
                .tags(getTagsByPostId(postId))
                .build();
        return ResponseEntity.ok(post);
    }

    @GetMapping(value = "/api/post/byDate")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> getPostsByDate(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "date") String date
    ) {
        List<Post> postListRep = postService.findAllPostByDate(ActivityStatus.ACTIVE, ModerationStatus.ACCEPTED, offset, limit, date);
        List<PostPublicDTO> posts = getPosts(postListRep);
        int count = postService.getTotalCountOfPostsByDate(ActivityStatus.ACTIVE, ModerationStatus.ACCEPTED, date);

        JSONObject response = new JSONObject();
        response.put("count", count);
        response.put("posts", posts);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/api/post/byTag")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> getPostsByTag(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "tag") String tag
    ) {
        List<Post> postListRep = postService.findAllPostByTag(ActivityStatus.ACTIVE, ModerationStatus.ACCEPTED, offset, limit, tag);
        List<PostPublicDTO> posts = getPosts(postListRep);
        int count = postService.getTotalCountOfPostsByTag(ActivityStatus.ACTIVE, ModerationStatus.ACCEPTED, tag);
        JSONObject response = new JSONObject();
        response.put("count", count);
        response.put("posts", posts);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/api/post/like")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> putLike(@RequestBody JSONObject request) {
        boolean result;
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
                postVoteService.setRating(userId, postId, Rating.LIKE);
                result = true;
            }
        } else {
            result = false;
        }
        JSONObject response = new JSONObject();
        response.put("result", result);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/api/post/dislike")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> putDislike(@RequestBody JSONObject request) {
        boolean result;
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
                postVoteService.setRating(userId, postId, Rating.DISLIKE);
                result = true;
            }
        } else {
            result = false;
        }
        JSONObject response = new JSONObject();
        response.put("result", result);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/api/post")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> addPost(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();
        boolean result;
        if (globalSettingsService.settingMultiUserModeIsEnabled()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:s");
            String postTimeString = request.get("time") + ":" + LocalDateTime.now(ZoneId.of("UTC")).getSecond(); // КОСТЫЛЬ
            int postActive = (int) request.get("active");
            String postTitle = (String) request.get("title");
            List<String> postTags = (ArrayList<String>) request.get("tags");
            String postText = (String) request.get("text");

            JSONObject message = new JSONObject();
            if (postTitle.isEmpty()) {
                message.put("message", "Заголовок не должен быть пустым!");
                message.put("result", false);
                return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
            }

            if (postText.isEmpty()) {
                message.put("message", "Пост не должен быть пустым!");
                message.put("result", false);
                return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
            }

            if (postTitle.length() < 3) {
                message.put("message", "Минимальное количество символов в заголовке - 3!");
                message.put("result", false);
                return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
            }

            if (postText.length() < 50) {
                message.put("message", "Минимальное количество символов в публикации - 50!");
                message.put("result", false);
                return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
            }

            LocalDateTime postTime;
            try {
                postTime = LocalDateTime.parse(postTimeString, formatter);
            } catch (DateTimeParseException e) {
                message.put("message", "Ошибка преобразования String в LocalDateTime");
                message.put("result", false);
                return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
            }
            if (postTime.isBefore(LocalDateTime.now(ZoneId.of("UTC")))) {
                postTime = LocalDateTime.now(ZoneId.of("UTC"));
            }

            ActivityStatus activity = postActive == 1 ? ActivityStatus.ACTIVE : ActivityStatus.INACTIVE;
            User user = userService.findById(authorizeServlet.getAuthorizedUserId());

            boolean moderation = globalSettingsService.settingPostPreModerationIsEnabled();
            Post newPost = postService.addPost(activity, user, postTime, postTitle, postText, moderation);

            for (String tagName : postTags) {
                Tag tag = tagService.createTagIfNoExistsAndReturn(tagName);
                tag2PostService.addTag2Post(newPost, tag);
            }

            result = true;
        } else {
            result = false;
        }

        response.put("result", result);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/api/post/moderation")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> listOfPostsForModeration(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "status") String status
    ) {
        long userId = authorizeServlet.getAuthorizedUserId();
        int count;
        List<Post> postListRep;
        switch (status) {
            case "declined":
                postListRep = postService.findAllPostsByModeratorId(ActivityStatus.ACTIVE, ModerationStatus.DECLINED, offset, limit, userId);
                count = postService.getTotalCountOfPostsByModeratorId(ActivityStatus.ACTIVE, ModerationStatus.DECLINED, userId);
                break;
            case "accepted":
                postListRep = postService.findAllPostsByModeratorId(ActivityStatus.ACTIVE, ModerationStatus.ACCEPTED, offset, limit, userId);
                count = postService.getTotalCountOfPostsByModeratorId(ActivityStatus.ACTIVE, ModerationStatus.ACCEPTED, userId);
                break;
            default:
                postListRep = postService.findAllNewPosts(ActivityStatus.ACTIVE, offset, limit);
                count = postService.getTotalCountOfNewPosts(ActivityStatus.ACTIVE);
        }
        List<PostPublicDTO> posts = getPosts(postListRep);
        JSONObject response = new JSONObject();
        response.put("count", count);
        response.put("posts", posts);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/api/post/my")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> getMyPosts(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "status") String status
    ) {
        long userId = authorizeServlet.getAuthorizedUserId();
        List<Post> postListRep;
        int count;
        switch (status) {
            case "inactive":
                postListRep = postService.findAllHiddenPostsByUserId(offset, limit, userId);
                count = postService.getTotalCountOfHiddenPostsByUserId(userId);
                break;
            case "pending":
                postListRep = postService.findAllPostsByUserId(ActivityStatus.ACTIVE, ModerationStatus.NEW, offset, limit, userId);
                count = postService.getTotalCountOfPostsByUserId(ActivityStatus.ACTIVE, ModerationStatus.NEW, userId);
                break;
            case "declined":
                postListRep = postService.findAllPostsByUserId(ActivityStatus.ACTIVE, ModerationStatus.DECLINED, offset, limit, userId);
                count = postService.getTotalCountOfPostsByUserId(ActivityStatus.ACTIVE, ModerationStatus.DECLINED, userId);
                break;
            default:
                postListRep = postService.findAllPostsByUserId(ActivityStatus.ACTIVE, ModerationStatus.ACCEPTED, offset, limit, userId);
                count = postService.getTotalCountOfPostsByUserId(ActivityStatus.ACTIVE, ModerationStatus.ACCEPTED, userId);
        }
        List<PostPublicDTO> posts = getPosts(postListRep);
        JSONObject response = new JSONObject();
        response.put("count", count);
        response.put("posts", posts);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/api/post/{id}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> updatePost(
            @PathVariable(value = "id") long postId,
            @RequestBody JSONObject request
    ) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String timeOfPostString = (String) request.get("time");
        int newPostActivity = (int) request.get("active");
        String newTitle = (String) request.get("title");
        List<String> newPostTags = (ArrayList<String>) request.get("tags");
        String newText = (String) request.get("text");

        JSONObject message = new JSONObject();
        if (newTitle.isEmpty()) {
            message.put("message", "Заголовок не должен быть пустым!");
            message.put("result", false);
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        if (newText.isEmpty()) {
            message.put("message", "Пост не должен быть пустым!");
            message.put("result", false);
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        if (newTitle.length() < 3) {
            message.put("message", "Минимальное количество символов в заголовке - 3!");
            message.put("result", false);
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        if (newText.length() < 50) {
            message.put("message", "Минимальное количество символов в публикации - 50!");
            message.put("result", false);
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        LocalDateTime newTimeOfPost;
        try {
            newTimeOfPost = LocalDateTime.parse(timeOfPostString, formatter);
        } catch (DateTimeParseException e) {
            message.put("message", "Необходимо указать дату!");
            message.put("result", false);
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
        if (newTimeOfPost.isBefore(LocalDateTime.now(ZoneId.of("UTC")))) {
            newTimeOfPost = LocalDateTime.now(ZoneId.of("UTC"));
        }

        ActivityStatus activityStatus = newPostActivity == 1 ? ActivityStatus.ACTIVE : ActivityStatus.INACTIVE;
        User user = userService.findById(authorizeServlet.getAuthorizedUserId());
        postService.updatePost(postId, user, activityStatus, newTimeOfPost, newTitle, newText);

        for (String tagName : newPostTags) {
            tagService.createTagIfNoExistsAndReturn(tagName);
        }
        tag2PostService.updateTagsByPostId(postId, newPostTags);

        JSONObject response = new JSONObject();
        response.put("result", true);
        return ResponseEntity.ok(response);
    }

    //==================================================================================================================

    private List<PostPublicDTO> getPosts(List<Post> postListRep) {
        List<PostPublicDTO> posts = new ArrayList<>();
        for (Post postRep : postListRep) {
            long postId = postRep.getId();
            long userId = postRep.getUser().getId();
            String userName = postRep.getUser().getName();

            PostPublicDTO postPublicDTO = PostPublicDTO.builder()
                    .id(postId)
                    .time(getStringTime(postRep.getTime()))
                    .title(postRep.getTitle())
                    .announce(getAnnounce(postRep.getText()))
                    .user(UserSimpleDTO.builder().id(userId).name(userName).build())
                    .likeCount(postVoteService.getCountLikesByPostId(postId))
                    .dislikeCount(postVoteService.getCountDislikesByPostId(postId))
                    .commentCount(postCommentService.getCountCommentsByPostId(postId))
                    .viewCount(postRep.getViewCount())
                    .build();
            posts.add(postPublicDTO);
        }
        return posts;
    }

    private List<String> getTagsByPostId(long postId) {
        List<Tag2Post> tag2PostListRep = tag2PostService.findAllTag2PostByPostId(postId);
        List<String> tags = new ArrayList<>();
        for (Tag2Post tag2PostRep : tag2PostListRep) {
            tags.add(tag2PostRep.getTag().getName());
        }
        return tags;
    }

    private List<CommentDTO> getCommentsByPostId(long postId) {
        List<PostComment> postCommentListRep = postCommentService.findAllPostCommentByPostId(postId);
        List<CommentDTO> commentDTOList = new ArrayList<>();

        for (PostComment postCommentRep : postCommentListRep) {
            long userId = postCommentRep.getUser().getId();
            String userName = postCommentRep.getUser().getName();
            String userPhoto = postCommentRep.getUser().getPhoto();
            UserWithPhotoDTO userWithPhoto = new UserWithPhotoDTO(userId, userName, userPhoto);

            CommentDTO commentDTO = CommentDTO.builder()
                    .id(postCommentRep.getId())
                    .time(getStringTime(postCommentRep.getTime()))
                    .text(postCommentRep.getText())
                    .user(userWithPhoto)
                    .build();

            //===========================================================
            commentDTOList.add(commentDTO);
        }

        return commentDTOList;
    }

    private String getAnnounce(String text) {
        int maxSizeAnnounce = 200;
        String announce = Jsoup.parse(text).text();
        if (announce.length() > maxSizeAnnounce) {
            announce = announce.substring(0, maxSizeAnnounce);
        }
        return announce;
    }

    private String getStringTime(LocalDateTime localDateTime) {
        ZonedDateTime localZone = localDateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime utcZone = localZone.withZoneSameInstant(ZoneId.of("UTC"));
        LocalDateTime utcTime = utcZone.toLocalDateTime();

        DateTimeFormatter simpleFormat = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter fullFormat = DateTimeFormatter.ofPattern("d MMM yyyy, EEE, HH:mm");
        LocalDateTime today = LocalDateTime.now(ZoneId.of("UTC"));
        LocalDateTime yesterday = today.minusDays(1);

        if (utcTime.getYear() == today.getYear() &&
                utcTime.getMonth() == today.getMonth() &&
                utcTime.getDayOfMonth() == today.getDayOfMonth()) {
            return "Сегодня, " + simpleFormat.format(utcTime);
        }

        if (utcTime.getYear() == yesterday.getYear() &&
                utcTime.getMonth() == yesterday.getMonth() &&
                utcTime.getDayOfMonth() == yesterday.getDayOfMonth()) {
            return "Вчера, " + simpleFormat.format(utcTime);
        }

        return fullFormat.format(utcTime);
    }

}
