package main.controller;

import main.model.entities.*;
import main.model.entities.enums.ActivesType;
import main.model.entities.enums.ModerationStatusType;
import main.responses.*;
import main.services.*;
import main.servlet.AuthorizeServlet;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ApiPostController {
    private AuthorizeServlet authorizeServlet;
    private PostService postService;
    private PostVoteService postVoteService;
    private PostCommentService postCommentService;
    private TagService tagService;
    private Tag2PostService tag2PostService;
    private UserService userService;

    @Autowired
    public ApiPostController(AuthorizeServlet authorizeServlet, PostService postService,
                             PostVoteService postVoteService, PostCommentService postCommentService,
                             TagService tagService, Tag2PostService tag2PostService,
                             UserService userService) {
        this.authorizeServlet = authorizeServlet;
        this.postService = postService;
        this.postVoteService = postVoteService;
        this.postCommentService = postCommentService;
        this.tagService = tagService;
        this.tag2PostService = tag2PostService;
        this.userService = userService;
    }

    //==================================================================================================================

    @GetMapping(value = "/api/post")
    @ResponseBody
    public ResponseEntity<CollectionPostsResponseDTO<ResponseDTO>> getAllPosts(
            @RequestParam(value = "offset") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "mode") String mode
    ) {
        List<Post> postListRep;
        switch (mode) {
            case "popular":
                postListRep = postService.findAllPostPopular(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, offset, limit);
                break;
            case "best":
                postListRep = postService.findAllPostBest(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, offset, limit);
                break;
            case "early":
                postListRep = postService.findAllPostSortedByDate(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, offset, limit, Sort.Direction.ASC);
                break;
            default:
                postListRep = postService.findAllPostSortedByDate(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, offset, limit, Sort.Direction.DESC);
        }

        List<ResponseDTO> posts = getPostsDTO(postListRep, PostInfoDTO.class);
        int count = postService.getTotalCountOfPosts(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED);

        return new ResponseEntity<>(new CollectionPostsResponseDTO<>(count, posts), HttpStatus.OK);
    }

    @GetMapping(value = "/api/post/search")
    @ResponseBody
    public ResponseEntity<CollectionPostsResponseDTO<ResponseDTO>> searchPost(
            @RequestParam(value = "offset") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "query") String query
    ) {
        List<Post> postListRep = query.equals("") ?
                postService.findAllPostSortedByDate(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, offset, limit, Sort.Direction.DESC) :
                postService.findAllPostByQuery(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, offset, limit, query);
        List<ResponseDTO> posts = getPostsDTO(postListRep, PostInfoDTO.class);
        int count = query.equals("") ?
                postService.getTotalCountOfPosts(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED) :
                postService.getTotalCountOfPostsByQuery(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, query);

        return new ResponseEntity<>(new CollectionPostsResponseDTO<>(count, posts), HttpStatus.OK);
    }

    @GetMapping(value = "/api/post/{id}")
    @ResponseBody
    public ResponseEntity<PostFullDTO> getPostById(@PathVariable(value = "id") long id) {
        Post postRep = postService.findById(id);
        long postId = postRep.getId();
        long userId = postRep.getUser().getId();
        String userName = postRep.getUser().getName();
        UserSimpleDTO userSimpleDTO = new UserSimpleDTO(userId, userName);

        PostFullDTO postFullDTO = new PostFullDTO();
        postFullDTO.setId(postId);
        postFullDTO.setTime(getStringTime(postRep.getTime()));
        postFullDTO.setUser(userSimpleDTO);
        postFullDTO.setTitle(postRep.getTitle());
        postFullDTO.setAnnounce(getAnnounce(postRep.getText()));
        postFullDTO.setLikeCount(postVoteService.getCountLikesByPostId(postId));
        postFullDTO.setDislikeCount(postVoteService.getCountDislikesByPostId(postId));
        postFullDTO.setCommentCount(postCommentService.getCountCommentsByPostId(postId));
        postFullDTO.setViewCount(postRep.getViewCount());
        postFullDTO.setComments(getCommentsByPostId(postId));
        postFullDTO.setTags(getTagsByPostId(postId));

        return new ResponseEntity<>(postFullDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/api/post/byDate")
    @ResponseBody
    public ResponseEntity<CollectionPostsResponseDTO<ResponseDTO>> getPostsByDate(
            @RequestParam(value = "offset") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "date") String date
    ) {
        List<Post> postListRep = postService.findAllPostByDate(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, offset, limit, date);
        List<ResponseDTO> posts = getPostsDTO(postListRep, PostInfoDTO.class);
        int count = postService.getTotalCountOfPostsByDate(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, date);

        return new ResponseEntity<>(new CollectionPostsResponseDTO<>(count, posts), HttpStatus.OK);
    }

    @GetMapping(value = "/api/post/byTag")
    @ResponseBody
    public ResponseEntity<CollectionPostsResponseDTO<ResponseDTO>> getPostsByTag(
            @RequestParam(value = "offset") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "tag") String tag
    ) {
        List<Post> postListRep = postService.findAllPostByTag(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, offset, limit, tag);
        List<ResponseDTO> posts = getPostsDTO(postListRep, PostInfoDTO.class);
        int count = postService.getTotalCountOfPostsByTag(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, tag);

        return new ResponseEntity<>(new CollectionPostsResponseDTO<>(count, posts), HttpStatus.OK);
    }

    @PostMapping(value = "/api/post/like")
    public ResponseEntity<JSONObject> putLike(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();
        boolean result;
        if (authorizeServlet.isUserAuthorize()) {
            long userId = authorizeServlet.getAuthorizedUserId();
            long postId = (int) request.get("post_id");
            if (postVoteService.userDislikeAlreadyExists(userId, postId)) {
                long postVoteId = postVoteService.getIdByUserIdAndPostId(userId, postId);
                postVoteService.replaceDislikeWithLike(postVoteId);
                result = true;
            } else if (postVoteService.userLikeAlreadyExists(userId, postId)) {
                long postVoteId = postVoteService.getIdByUserIdAndPostId(userId, postId);
                postVoteService.deleteById(postVoteId);
                result = false;
            } else {
                User user = userService.findById(userId);
                Post post = postService.findById(postId);
                PostVote postVote = new PostVote();
                postVote.setUser(user);
                postVote.setPost(post);
                postVote.setTime(LocalDateTime.now());
                postVote.setValue((byte) 1);
                postVoteService.addPostVote(postVote);
                result = true;
            }
        } else {
            result = false;
        }
        response.put("result", result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/api/post/dislike")
    public ResponseEntity<JSONObject> putDislike(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();
        boolean result;
        if (authorizeServlet.isUserAuthorize()) {
            long userId = authorizeServlet.getAuthorizedUserId();
            long postId = (int) request.get("post_id");
            if (postVoteService.userLikeAlreadyExists(userId, postId)) {
                long postVoteId = postVoteService.getIdByUserIdAndPostId(userId, postId);
                postVoteService.replaceLikeWithDislike(postVoteId);
                result = true;
            } else if (postVoteService.userDislikeAlreadyExists(userId, postId)) {
                long postVoteId = postVoteService.getIdByUserIdAndPostId(userId, postId);
                postVoteService.deleteById(postVoteId);
                result = false;
            } else {
                User user = userService.findById(userId);
                Post post = postService.findById(postId);
                PostVote postVote = new PostVote();
                postVote.setUser(user);
                postVote.setPost(post);
                postVote.setTime(LocalDateTime.now());
                postVote.setValue((byte) -1);
                postVoteService.addPostVote(postVote);
                result = true;
            }
        } else {
            result = false;
        }
        response.put("result", result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/api/post")
    public ResponseEntity addPost(@RequestBody JSONObject request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String postTimeString = (String) request.get("time");
        int postActive = (int) request.get("active");
        String postTitle = (String) request.get("title");
        List<String> postTags = (ArrayList<String>) request.get("tags");
        String postText = (String) request.get("text");

        JSONObject message = new JSONObject();
        if (postTitle.isEmpty()) {
            message.put("message", "Заголовок не должен быть пустым!");
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        if (postText.isEmpty()) {
            message.put("message", "Пост не должен быть пустым!");
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        if (postTitle.length() < 3) {
            message.put("message", "Минимальное количество символов в заголовке - 3!");
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        if (postText.length() < 50) {
            message.put("message", "Минимальное количество символов в публикации - 50!");
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        LocalDateTime postTime;
        try {
            postTime = LocalDateTime.parse(postTimeString, formatter);
        } catch (DateTimeParseException e) {
            return new ResponseEntity("Ошибка преобразования String в LocalDateTime", HttpStatus.BAD_REQUEST);
        }
        if (postTime.isBefore(LocalDateTime.now())) {
            postTime = LocalDateTime.now();
        }

        byte isActive = postActive == 1 ? (byte) 1 : 0;
        User user = userService.findById(authorizeServlet.getAuthorizedUserId());

        Post newPost = new Post();
        newPost.setIsActive(isActive);
        newPost.setUser(user);
        newPost.setTime(postTime);
        newPost.setTitle(postTitle);
        newPost.setText(postText);
        newPost = postService.addPostAndReturn(newPost);

        for (String tagName : postTags) {
            Tag tag = tagService.createTagIfNoExistsAndReturn(tagName);

            //TODO: Поместить создание объекта внутрь метода сервиса
            Tag2Post tag2Post = new Tag2Post();
            tag2Post.setPost(newPost);
            tag2Post.setTag(tag);
            tag2PostService.addTag2Post(tag2Post);
        }

        JSONObject response = new JSONObject();
        response.put("result", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/post/moderation")
    @ResponseBody
    public ResponseEntity listOfPostsForModeration(
            @RequestParam(value = "offset") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "status") String status
    ) {
        long userId = authorizeServlet.getAuthorizedUserId();
        int count;
        List<Post> postListRep;
        switch (status) {
            case "declined":
                postListRep = postService.findAllPostsByModeratorId(ActivesType.ACTIVE, ModerationStatusType.DECLINED, offset, limit, userId);
                count = postService.getTotalCountOfPostsByModeratorId(ActivesType.ACTIVE, ModerationStatusType.DECLINED, userId);
                break;
            case "accepted":
                postListRep = postService.findAllPostsByModeratorId(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, offset, limit, userId);
                count = postService.getTotalCountOfPostsByModeratorId(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, userId);
                break;
            default:
                postListRep = postService.findAllNewPosts(ActivesType.ACTIVE, offset, limit);
                count = postService.getTotalCountOfNewPosts(ActivesType.ACTIVE);
        }

        List<ResponseDTO> posts = getPostsDTO(postListRep, PostInfoDTO.class);

        return new ResponseEntity<>(new CollectionPostsResponseDTO<>(count, posts), HttpStatus.OK);
    }

    @GetMapping(value = "/api/post/my")
    @ResponseBody
    public ResponseEntity getMyPosts(
            @RequestParam(value = "offset") int offset,
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
                postListRep = postService.findAllPostsByUserId(ActivesType.ACTIVE, ModerationStatusType.NEW, offset, limit, userId);
                count = postService.getTotalCountOfPostsByUserId(ActivesType.ACTIVE, ModerationStatusType.NEW, userId);
                break;
            case "declined":
                postListRep = postService.findAllPostsByUserId(ActivesType.ACTIVE, ModerationStatusType.DECLINED, offset, limit, userId);
                count = postService.getTotalCountOfPostsByUserId(ActivesType.ACTIVE, ModerationStatusType.DECLINED, userId);
                break;
            default:
                postListRep = postService.findAllPostsByUserId(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, offset, limit, userId);
                count = postService.getTotalCountOfPostsByUserId(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, userId);
        }

        List<ResponseDTO> posts = getPostsDTO(postListRep, PostInfoDTO.class);

        return new ResponseEntity<>(new CollectionPostsResponseDTO<>(count, posts), HttpStatus.OK);
    }

    @PutMapping(value = "/api/post/{id}")
    @ResponseBody
    public ResponseEntity updatePost(
            @PathVariable(value = "id") long postId,
            @RequestBody JSONObject request
    ) {
        Post updatedPost = postService.findById(postId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String timeOfPostString = (String) request.get("time");
        int newPostActivity = (int) request.get("active");
        String newTitle = (String) request.get("title");
        List<String> newPostTags = (ArrayList<String>) request.get("tags");
        String newText = (String) request.get("text");

        JSONObject message = new JSONObject();
        if (newTitle.isEmpty()) {
            message.put("message", "Заголовок не должен быть пустым!");
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        if (newText.isEmpty()) {
            message.put("message", "Пост не должен быть пустым!");
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        if (newTitle.length() < 3) {
            message.put("message", "Минимальное количество символов в заголовке - 3!");
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        if (newText.length() < 50) {
            message.put("message", "Минимальное количество символов в публикации - 50!");
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        LocalDateTime newTimeOfPost;
        try {
            newTimeOfPost = LocalDateTime.parse(timeOfPostString, formatter);
        } catch (DateTimeParseException e) {
            message.put("message", "Необходимо указать дату!");
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
        if (newTimeOfPost.isBefore(LocalDateTime.now())) {
            newTimeOfPost = LocalDateTime.now();
        }

        byte isActive = newPostActivity == 1 ? (byte) 1 : 0;

        User user = userService.findById(authorizeServlet.getAuthorizedUserId());
        if (user.getIsModerator() == (byte) 1) {
            updatedPost.setModerator(user);
        } else {
            updatedPost.setModerationStatus(ModerationStatusType.NEW);
        }

        updatedPost.setIsActive(isActive);
        updatedPost.setTime(newTimeOfPost);
        updatedPost.setTitle(newTitle);
        updatedPost.setText(newText);

        for (String tagName : newPostTags) {
            tagService.createTagIfNoExistsAndReturn(tagName);
        }
        tag2PostService.updateTagsByPostId(postId, newPostTags);

        JSONObject response = new JSONObject();
        response.put("result", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
//        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    //==================================================================================================================

    private <T extends ResponseDTO> List<ResponseDTO> getPostsDTO(List<Post> postListRep, Class<T> postDTO) {
        List<ResponseDTO> posts = new ArrayList<>();
        for (Post postRep : postListRep) {
            long postId = postRep.getId();
            long userId = postRep.getUser().getId();
            String userName = postRep.getUser().getName();
            UserSimpleDTO user = new UserSimpleDTO(userId, userName);
            PostSimpleDTO postSimpleDTO = new PostSimpleDTO();
            postSimpleDTO.setId(postId);
            postSimpleDTO.setTime(getStringTime(postRep.getTime()));
            postSimpleDTO.setUser(user);
            postSimpleDTO.setTitle(postRep.getTitle());
            postSimpleDTO.setAnnounce(getAnnounce(postRep.getText()));
            if (postDTO.getSuperclass() == PostSimpleDTO.class || postDTO.getSuperclass() == PostInfoDTO.class) {
                PostInfoDTO postInfoDTO = new PostInfoDTO(postSimpleDTO);
                postInfoDTO.setLikeCount(postVoteService.getCountLikesByPostId(postId));
                postInfoDTO.setDislikeCount(postVoteService.getCountDislikesByPostId(postId));
                postInfoDTO.setCommentCount(postCommentService.getCountCommentsByPostId(postId));
                postInfoDTO.setViewCount(postRep.getViewCount());

                if (postDTO.getSuperclass() == PostInfoDTO.class) {
                    PostFullDTO postFullDTO = new PostFullDTO(postSimpleDTO, postInfoDTO);
                    postFullDTO.setComments(getCommentsByPostId(postId));
                    postFullDTO.setTags(getTagsByPostId(postId));
                } else {
                    posts.add(postInfoDTO);
                }
            } else {
                posts.add(postSimpleDTO);
            }
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

            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setId(postCommentRep.getId());
            commentDTO.setTime(getStringTime(postCommentRep.getTime()));
            commentDTO.setText(postCommentRep.getText());
            commentDTO.setUser(userWithPhoto);

            //===========================================================
            commentDTOList.add(commentDTO);
        }

        return commentDTOList;
    }

    private String getAnnounce(String text) {
        int maxSizeAnnounce = 200;
        String announce = text;
        if (announce.length() > maxSizeAnnounce) {
            announce = announce.substring(0, maxSizeAnnounce);
        }
        return announce;
    }

    private String getStringTime(LocalDateTime localDateTime) {
        DateTimeFormatter simpleFormat = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter fullFormat = DateTimeFormatter.ofPattern("d MMM yyyy, EEE, HH:mm");
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime yesterday = today.minusDays(1);

        if (localDateTime.getYear() == today.getYear() &&
                localDateTime.getMonth() == today.getMonth() &&
                localDateTime.getDayOfMonth() == today.getDayOfMonth()) {
            return "Сегодня, " + simpleFormat.format(localDateTime);
        }

        if (localDateTime.getYear() == yesterday.getYear() &&
                localDateTime.getMonth() == yesterday.getMonth() &&
                localDateTime.getDayOfMonth() == yesterday.getDayOfMonth()) {
            return "Вчера, " + simpleFormat.format(localDateTime);
        }

        return fullFormat.format(localDateTime);
    }

}
