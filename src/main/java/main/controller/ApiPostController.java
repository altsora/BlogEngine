package main.controller;

import main.model.entities.enums.ModerationStatusType;
import main.model.entities.Post;
import main.model.entities.PostComment;
import main.model.entities.Tag2Post;
import main.model.repositories.*;
import main.model.responses.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ApiPostController {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostVoteRepository postVoteRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private Tag2PostRepository tag2PostRepository;

    @GetMapping(value = "/api/post")
    @ResponseBody
    public CollectionPostsResponseDTO<PostInfoDTO> getAllPosts(
            @RequestParam(value = "offset") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "mode") String mode
    ) {
        List<Post> postListRep;
        switch (mode) {
            case "popular":
                postListRep = postRepository.findAllPostPopular((byte) 1, ModerationStatusType.ACCEPTED);
                break;
            case "early":
                postListRep = postRepository.findAllPostEarly((byte) 1, ModerationStatusType.ACCEPTED);
                break;
            case "best":
                postListRep = postRepository.findAllPostBest((byte) 1, ModerationStatusType.ACCEPTED);
                break;
            default:
                postListRep = postRepository.findAllPostRecent((byte) 1, ModerationStatusType.ACCEPTED);
        }

        long allPostsCount = postListRep.size();
        long minCountPostsOnPage = Math.min(limit, allPostsCount);
        List<PostInfoDTO> posts = new ArrayList<>();
        for (int i = offset; i < minCountPostsOnPage + offset; i++) {
            if (i == allPostsCount) {
                break;
            }
            Post postRep = postListRep.get(i);
            int postId = postRep.getId();
            int userId = postRep.getUser().getId();
            String userName = postRep.getUser().getName();
            UserSimple user = new UserSimple(userId, userName);

            PostInfoDTO postInfoDTO = new PostInfoDTO();
            postInfoDTO.setId(postId);
            postInfoDTO.setTime(getStringTime(postRep.getTime()));
            postInfoDTO.setUser(user);
            postInfoDTO.setTitle(postRep.getTitle());
            postInfoDTO.setAnnounce(getAnnounce(postRep.getText()));
            postInfoDTO.setLikeCount(postVoteRepository.getCountLikesByPostId(postId));
            postInfoDTO.setDislikeCount(postVoteRepository.getCountDislikesByPostId(postId));
            postInfoDTO.setCommentCount(postCommentRepository.getCountCommentsByPostId(postId));
            postInfoDTO.setViewCount(postRep.getViewCount());
            //===========================================================
            posts.add(postInfoDTO);
        }

        CollectionPostsResponseDTO<PostInfoDTO> collectionPostsResponseDTO = new CollectionPostsResponseDTO<>();
        collectionPostsResponseDTO.setCount(allPostsCount);
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
        List<Post> postListRep = (query.equals("")) ?
                postRepository.findAllPostRecent((byte)1, ModerationStatusType.ACCEPTED) :
                postRepository.findAllPostRecentByQuery((byte)1, ModerationStatusType.ACCEPTED, query);

        List<PostInfoDTO> posts = new ArrayList<>();
        long allPostsCount = postListRep.size();
        long minCountPostsOnPage = Math.min(limit, allPostsCount);
        for (int i = offset; i < minCountPostsOnPage + offset; i++) {
            if (i == allPostsCount) {
                break;
            }
            Post postRep = postListRep.get(i);
            int postId = postRep.getId();
            int userId = postRep.getUser().getId();
            String userName = postRep.getUser().getName();
            UserSimple user = new UserSimple(userId, userName);

            PostInfoDTO postInfoDTO = new PostInfoDTO();
            postInfoDTO.setId(postId);
            postInfoDTO.setTime(getStringTime(postRep.getTime()));
            postInfoDTO.setUser(user);
            postInfoDTO.setTitle(postRep.getTitle());
            postInfoDTO.setAnnounce(getAnnounce(postRep.getText()));
            postInfoDTO.setLikeCount(postVoteRepository.getCountLikesByPostId(postId));
            postInfoDTO.setDislikeCount(postVoteRepository.getCountDislikesByPostId(postId));
            postInfoDTO.setCommentCount(postCommentRepository.getCountCommentsByPostId(postId));
            postInfoDTO.setViewCount(postRep.getViewCount());

            //===========================================================
            posts.add(postInfoDTO);
        }

        CollectionPostsResponseDTO<PostInfoDTO> collectionPostsResponseDTO = new CollectionPostsResponseDTO<>();
        collectionPostsResponseDTO.setCount(allPostsCount);
        collectionPostsResponseDTO.setPosts(posts);

        return collectionPostsResponseDTO;
    }

    @GetMapping(value = "/api/post/{id}")
    @ResponseBody
    public PostFullDTO getPostById(@PathVariable(value = "id") int id) {
        Post postRep = postRepository.findPostById(id, (byte) 1, ModerationStatusType.ACCEPTED);
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
        postFullDTO.setLikeCount(postVoteRepository.getCountLikesByPostId(postId));
        postFullDTO.setDislikeCount(postVoteRepository.getCountDislikesByPostId(postId));
        postFullDTO.setCommentCount(postCommentRepository.getCountCommentsByPostId(postId));
        postFullDTO.setViewCount(postRep.getViewCount());
        postFullDTO.setComments(getCommentsByPostId(postId));
        postFullDTO.setTags(getTagsByPostId(postId));

        return postFullDTO;
    }

    @GetMapping(value = "/api/post/byDate")
    @ResponseBody
    public CollectionPostsResponseDTO getPostsByDate(
            @RequestParam(value = "offset") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "date") String date
    ) {
        if (!date.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
            return new CollectionPostsResponseDTO();
        }
        String[] var = date.split("-");
        int year = Integer.parseInt(var[0]);
        int month = Integer.parseInt(var[1]);
        int dayOfMonth = Integer.parseInt(var[2]);
        List<Post> postListRep = postRepository.findAllPostByDate((byte)1, ModerationStatusType.ACCEPTED, year, month, dayOfMonth);

        List<PostInfoDTO> posts = new ArrayList<>();
        long allPostsCount = postListRep.size();
        long minCountPostsOnPage = Math.min(limit, allPostsCount);
        for (int i = offset; i < minCountPostsOnPage + offset; i++) {
            if (i == allPostsCount) {
                break;
            }
            Post postRep = postListRep.get(i);
            int postId = postRep.getId();
            int userId = postRep.getUser().getId();
            String userName = postRep.getUser().getName();
            UserSimple user = new UserSimple(userId, userName);

            PostInfoDTO postInfoDTO = new PostInfoDTO();
            postInfoDTO.setId(postId);
            postInfoDTO.setTime(getStringTime(postRep.getTime()));
            postInfoDTO.setUser(user);
            postInfoDTO.setTitle(postRep.getTitle());
            postInfoDTO.setAnnounce(getAnnounce(postRep.getText()));
            postInfoDTO.setLikeCount(postVoteRepository.getCountLikesByPostId(postId));
            postInfoDTO.setDislikeCount(postVoteRepository.getCountDislikesByPostId(postId));
            postInfoDTO.setCommentCount(postCommentRepository.getCountCommentsByPostId(postId));
            postInfoDTO.setViewCount(postRep.getViewCount());

            //===========================================================
            posts.add(postInfoDTO);
        }

        CollectionPostsResponseDTO<PostInfoDTO> collectionPostsResponseDTO = new CollectionPostsResponseDTO<>();
        collectionPostsResponseDTO.setCount(allPostsCount);
        collectionPostsResponseDTO.setPosts(posts);

        return collectionPostsResponseDTO;
    }
    
    @GetMapping(value = "/api/tag")
    public ResponseEntity getTag() {
        return ResponseEntity.status(HttpStatus.OK).body(null);
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

        for(PostComment postCommentRep : postCommentListRep) {
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
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime yesterday = today.minusDays(1);

        if (localDateTime.getYear() == today.getYear() &&
                localDateTime.getMonth() == today.getMonth() &&
                localDateTime.getDayOfMonth() == today.getDayOfMonth()) {
            return "Сегодня, " + formatter1.format(localDateTime);
        }

        if (localDateTime.getYear() == yesterday.getYear() &&
                localDateTime.getMonth() == yesterday.getMonth() &&
                localDateTime.getDayOfMonth() == yesterday.getDayOfMonth()) {
            return "Вчера, " + formatter1.format(localDateTime);
        }

        return formatter2.format(localDateTime);
    }
}
