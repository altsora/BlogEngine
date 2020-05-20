package main.controller;

import main.model.entities.Tag;
import main.model.entities.enums.ActivesType;
import main.model.entities.enums.ModerationStatusType;
import main.model.entities.Post;
import main.model.entities.PostComment;
import main.model.entities.Tag2Post;
import main.model.repositories.*;
import main.model.responses.*;
import main.model.services.PostService;
import main.model.services.PostVoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ApiPostController {

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private Tag2PostRepository tag2PostRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private PostVoteService postVoteService;

    @GetMapping(value = "/api/post")
    @ResponseBody
    public CollectionPostsResponseDTO<ResponseDTO> getAllPosts(
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
        int count = postService.getTotalNumberOfPosts(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED);

        CollectionPostsResponseDTO<ResponseDTO> collectionPostsResponseDTO = new CollectionPostsResponseDTO<>();
        collectionPostsResponseDTO.setCount(count);
        collectionPostsResponseDTO.setPosts(posts);

        return collectionPostsResponseDTO;
    }

    @GetMapping(value = "/api/post/search")
    @ResponseBody
    public CollectionPostsResponseDTO searchPost(
            @RequestParam(value = "offset") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "query") String query
    ) {
        List<Post> postListRep = query.equals("") ?
                postService.findAllPostSortedByDate(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, offset, limit, Sort.Direction.DESC) :
                postService.findAllPostByQuery(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, offset, limit, query);
        List<ResponseDTO> posts = getPostsDTO(postListRep, PostInfoDTO.class);
        int count = query.equals("") ?
                postService.getTotalNumberOfPosts(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED) :
                postService.getTotalNumberOfPostsByQuery(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, query);

        CollectionPostsResponseDTO<ResponseDTO> collectionPostsResponseDTO = new CollectionPostsResponseDTO<>();
        collectionPostsResponseDTO.setCount(count);
        collectionPostsResponseDTO.setPosts(posts);

        return collectionPostsResponseDTO;

    }

    @GetMapping(value = "/api/post/{id}")
    @ResponseBody
    public PostFullDTO getPostById(@PathVariable(value = "id") int id) {
        Post postRep = postService.findPostById(id, ActivesType.ACTIVE, ModerationStatusType.ACCEPTED);
        int postId = postRep.getId();
        int userId = postRep.getUser().getId();
        String userName = postRep.getUser().getName();
        UserSimple userSimple = new UserSimple(userId, userName);

        PostFullDTO postFullDTO = new PostFullDTO();
        postFullDTO.setId(postId);
        postFullDTO.setTime(getStringTime(postRep.getTime()));
        postFullDTO.setUser(userSimple);
        postFullDTO.setTitle(postRep.getTitle());
        postFullDTO.setAnnounce(getAnnounce(postRep.getText()));
        postFullDTO.setLikeCount(postVoteService.getCountLikesByPostId(postId));
        postFullDTO.setDislikeCount(postVoteService.getCountDislikesByPostId(postId));
        postFullDTO.setCommentCount(postCommentRepository.getCountCommentsByPostId(postId));
        postFullDTO.setViewCount(postRep.getViewCount());
        postFullDTO.setComments(getCommentsByPostId(postId));
        postFullDTO.setTags(getTagsByPostId(postId));

        return postFullDTO;
    }

    @GetMapping(value = "/api/post/byDate")
    @ResponseBody
    public CollectionPostsResponseDTO<ResponseDTO> getPostsByDate(
            @RequestParam(value = "offset") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "date") String date
    ) {
        List<Post> postListRep = postService.findAllPostByDate(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, offset, limit, date);
        List<ResponseDTO> posts = getPostsDTO(postListRep, PostInfoDTO.class);
        int count = postService.getTotalNumberOfPostsByDate(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, date);

        CollectionPostsResponseDTO<ResponseDTO> collectionPostsResponseDTO = new CollectionPostsResponseDTO<>();
        collectionPostsResponseDTO.setCount(count);
        collectionPostsResponseDTO.setPosts(posts);

        return collectionPostsResponseDTO;
    }

    @GetMapping(value = "/api/post/byTag")
    @ResponseBody
    public CollectionPostsResponseDTO<ResponseDTO> getPostsByTag(
            @RequestParam(value = "offset") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "tag") String tag
    ) {
        List<Post> postListRep = postService.findAllPostByTag(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, offset, limit, tag);
        List<ResponseDTO> posts = getPostsDTO(postListRep, PostInfoDTO.class);
        int count = postService.getTotalNumberOfPostsByTag(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, tag);

        CollectionPostsResponseDTO<ResponseDTO> collectionPostsResponseDTO = new CollectionPostsResponseDTO<>();
        collectionPostsResponseDTO.setCount(count);
        collectionPostsResponseDTO.setPosts(posts);

        return collectionPostsResponseDTO;
    }


    @GetMapping(value = "/api/tag")
    @ResponseBody
    public CollectionTagsResponseDTO getTagList(@RequestParam(value = "query", required = false) String query) {
        List<Tag> tagListRep = (query == null || query.equals("")) ?
                tagRepository.findAll() :
                tagRepository.findAllTagsByQuery(query);
        List<Double> weights = new ArrayList<>();
        int totalNumberOfPosts = postService.getTotalNumberOfPosts(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED);
        double maxWeight = -1;
        for (Tag tagRep : tagListRep) {
            int countPosts = postService.getTotalNumberOfPostsByTag(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, tagRep.getName());
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
        return collectionTagsResponseDTO;
    }

    private <T extends ResponseDTO> List<ResponseDTO> getPostsDTO(List<Post> postListRep, Class<T> classDTO) {
        List<ResponseDTO> posts = new ArrayList<>();
        for (Post postRep : postListRep) {
            int postId = postRep.getId();
            int userId = postRep.getUser().getId();
            String userName = postRep.getUser().getName();
            UserSimple user = new UserSimple(userId, userName);
            PostSimpleDTO postSimpleDTO = new PostSimpleDTO();
            postSimpleDTO.setId(postId);
            postSimpleDTO.setTime(getStringTime(postRep.getTime()));
            postSimpleDTO.setUser(user);
            postSimpleDTO.setTitle(postRep.getTitle());
            postSimpleDTO.setAnnounce(getAnnounce(postRep.getText()));
            if (classDTO.getSuperclass() == PostSimpleDTO.class || classDTO.getSuperclass() == PostInfoDTO.class) {
                PostInfoDTO postInfoDTO = new PostInfoDTO(postSimpleDTO);
                postInfoDTO.setLikeCount(postVoteService.getCountLikesByPostId(postId));
                postInfoDTO.setDislikeCount(postVoteService.getCountDislikesByPostId(postId));
                postInfoDTO.setCommentCount(postCommentRepository.getCountCommentsByPostId(postId));
                postInfoDTO.setViewCount(postRep.getViewCount());

                if (classDTO.getSuperclass() == PostInfoDTO.class) {
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

    private List<String> getTagsByPostId(int postId) {
        List<Tag2Post> tag2PostListRep = tag2PostRepository.findAllTag2PostByPostId(postId);
        List<String> tags = new ArrayList<>();
        for (Tag2Post tag2PostRep : tag2PostListRep) {
            tags.add(tag2PostRep.getTag().getName());
        }
        return tags;
    }

    private List<CommentDTO> getCommentsByPostId(int postId) {
        List<PostComment> postCommentListRep = postCommentRepository.findAllPostCommentByPostId(postId);
        List<CommentDTO> commentDTOList = new ArrayList<>();

        for (PostComment postCommentRep : postCommentListRep) {
            int userId = postCommentRep.getUser().getId();
            String userName = postCommentRep.getUser().getName();
            String userPhoto = postCommentRep.getUser().getPhoto();
            UserWithPhoto userWithPhoto = new UserWithPhoto(userId, userName, userPhoto);

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
