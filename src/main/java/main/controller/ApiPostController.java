package main.controller;

import main.model.entities.Tag;
import main.model.entities.enums.ActivesType;
import main.model.entities.enums.ModerationStatusType;
import main.model.entities.Post;
import main.model.entities.PostComment;
import main.model.entities.Tag2Post;
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

    @Autowired
    public ApiPostController(AuthorizeServlet authorizeServlet, PostService postService,
                             PostVoteService postVoteService, PostCommentService postCommentService,
                             TagService tagService, Tag2PostService tag2PostService) {
        this.authorizeServlet = authorizeServlet;
        this.postService = postService;
        this.postVoteService = postVoteService;
        this.postCommentService = postCommentService;
        this.tagService = tagService;
        this.tag2PostService = tag2PostService;
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
        Post postRep = postService.findPostByPostId(id, ActivesType.ACTIVE, ModerationStatusType.ACCEPTED);
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

    @GetMapping(value = "/api/tag")
    @ResponseBody
    public ResponseEntity<CollectionTagsResponseDTO> getTagList(@RequestParam(value = "query", required = false) String query) {
        List<Tag> tagListRep = (query == null || query.equals("")) ?
                tagService.findAll() :
                tagService.findAllTagsByQuery(query);
        List<Double> weights = new ArrayList<>();
        int totalNumberOfPosts = postService.getTotalCountOfPosts(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED);
        double maxWeight = -1;
        for (Tag tagRep : tagListRep) {
            int countPosts = postService.getTotalCountOfPostsByTag(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, tagRep.getName());
            double weight = (double) countPosts / totalNumberOfPosts;
            weights.add(weight);
            if (weight > maxWeight) {
                maxWeight = weight;
            }
        }

        List<TagDTO> tags = new ArrayList<>();
        for (int i = 0; i < tagListRep.size(); i++) {
            String tagName = tagListRep.get(i).getName();
            double normalizedWeight = weights.get(i) / maxWeight;
            TagDTO tagDTO = new TagDTO(tagName, normalizedWeight);
            tags.add(tagDTO);
        }

        CollectionTagsResponseDTO collectionTagsResponseDTO = new CollectionTagsResponseDTO();
        collectionTagsResponseDTO.setTags(tags);
        return new ResponseEntity<>(collectionTagsResponseDTO, HttpStatus.OK);
    }

    @PostMapping(value = "/api/post/like")
    public ResponseEntity putLike(@RequestBody(required = false) JSONObject request) {
        JSONObject response = new JSONObject();
        boolean result;
        if (authorizeServlet.isUserAuthorize()) {
            long userId = authorizeServlet.getAuthorizedUserId();
            long postId = (int) request.get("post_id");
            if (postVoteService.userLikeAlreadyExists(userId, postId)) {
                int postVoteId = postVoteService.getIdByUserIdAndPostId(userId, postId);
                postVoteService.deleteById(postVoteId);
                result = false;
            } else {
                result = true;
            }

        } else {
            result = false;
        }
        response.put("result", result);
        return new ResponseEntity(response, HttpStatus.OK);

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

    private static String getStringTime(LocalDateTime localDateTime) {
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
