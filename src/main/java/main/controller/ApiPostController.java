package main.controller;

import main.model.entities.enums.ModerationStatusType;
import main.model.entities.Post;
import main.model.entities.PostComment;
import main.model.entities.Tag2Post;
import main.model.repositories.*;
import main.model.responses.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        int pageNumber = offset / limit;
        switch (mode) {
            case "popular":
                Pageable sortedByCountComment = PageRequest.of(pageNumber, limit, Sort.by("countComments").descending());
                postListRep = postRepository.findAllPostPopular((byte) 1, ModerationStatusType.ACCEPTED, sortedByCountComment);
                break;
            case "best":
                Pageable sortedByCountLikes = PageRequest.of(pageNumber, limit, Sort.by("countLikes").descending());
                postListRep = postRepository.findAllPostBest((byte) 1, ModerationStatusType.ACCEPTED, sortedByCountLikes);
                break;
            case "early":
                Pageable sortedByPostTimeAsc = PageRequest.of(pageNumber, limit, Sort.by("time"));
                postListRep = postRepository.findAllPostSortedByDate((byte) 1, ModerationStatusType.ACCEPTED, sortedByPostTimeAsc);
                break;
            default:
                Pageable sortedByPostTimeDesc = PageRequest.of(pageNumber, limit, Sort.by("time").descending());
                postListRep = postRepository.findAllPostSortedByDate((byte) 1, ModerationStatusType.ACCEPTED, sortedByPostTimeDesc);
        }

        List<PostInfoDTO> posts = new ArrayList<>();
        for (Post postRep : postListRep) {
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
        int count = postRepository.getTotalNumberOfPosts((byte) 1, ModerationStatusType.ACCEPTED);
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
        int pageNumber = offset / limit;
        Pageable sortedByPostTimeAsc = PageRequest.of(pageNumber, limit, Sort.by("time"));
        List<Post> postListRep = (query.equals("")) ?
                postRepository.findAllPostRecent((byte)1, ModerationStatusType.ACCEPTED, sortedByPostTimeAsc) :
                postRepository.findAllPostByQuery((byte)1, ModerationStatusType.ACCEPTED, query, sortedByPostTimeAsc);

        List<PostInfoDTO> posts = new ArrayList<>();
        for (Post postRep : postListRep) {
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
        //TODO: Проверить запрос
        int count = postRepository.getTotalNumberOfPosts((byte) 1, ModerationStatusType.ACCEPTED);
        collectionPostsResponseDTO.setCount(count);
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
        String[] var = date.split("-");
        int year = Integer.parseInt(var[0]);
        int month = Integer.parseInt(var[1]);
        int dayOfMonth = Integer.parseInt(var[2]);

        int pageNumber = offset / limit;
        Pageable sortedByPostTimeAsc = PageRequest.of(pageNumber, limit, Sort.by("time"));
        List<Post> postListRep = postRepository.findAllPostByDate(
                (byte)1, ModerationStatusType.ACCEPTED,
                year, month, dayOfMonth, sortedByPostTimeAsc
        );

        List<PostInfoDTO> posts = new ArrayList<>();
        for (Post postRep : postListRep) {
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
        int count = postRepository.getTotalNumberOfPostsByDate((byte) 1, ModerationStatusType.ACCEPTED, year, month, dayOfMonth);
        collectionPostsResponseDTO.setCount(count);
        collectionPostsResponseDTO.setPosts(posts);

        return collectionPostsResponseDTO;
    }

    @GetMapping(value = "/api/post/byTag")
    @ResponseBody
    public CollectionPostsResponseDTO getPostsByTag(
            @RequestParam(value = "offset") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "tag") String tag
    ) {
        int pageNumber = offset / limit;
        Pageable sortedByPostTimeAsc = PageRequest.of(pageNumber, limit, Sort.by("time"));
        List<Post> postListRep = postRepository.findAllPostByTag((byte) 1, ModerationStatusType.ACCEPTED, tag, sortedByPostTimeAsc);
        List<PostInfoDTO> posts = new ArrayList<>();
        for (Post postRep : postListRep) {
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
        int count = postRepository.getTotalNumberOfPostsByTag((byte) 1, ModerationStatusType.ACCEPTED, tag);
        collectionPostsResponseDTO.setCount(count);
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
