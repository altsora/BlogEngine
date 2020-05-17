package main.controller;

import main.model.ModerationStatusType;
import main.model.entities.Post;
import main.model.entities.PostComment;
import main.model.entities.PostVote;
import main.model.repositories.PostCommentRepository;
import main.model.repositories.PostRepository;
import main.model.repositories.PostVoteRepository;
import main.model.repositories.UserRepository;
import main.model.responses.CollectionPostsResponseDTO;
import main.model.responses.PostInfoDTO;
import main.model.responses.UserSimple;
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
            PostInfoDTO postInfoDTO = new PostInfoDTO();

            Post postRep = postListRep.get(i);
            postInfoDTO.setId(postRep.getId());
            postInfoDTO.setTitle(postRep.getTitle());
            postInfoDTO.setViewCount(postRep.getViewCount());

            UserSimple user = new UserSimple();
            user.setId(postRep.getUser().getId());
            user.setName(postRep.getUser().getName());
            postInfoDTO.setUser(user);

            String time = getStringTime(postRep.getTime());
            postInfoDTO.setTime(time);

            int postId = postRep.getId();
            // Likes/Dislikes
            List<PostVote> postVoteListRep = postVoteRepository.findAllPostVotesByPostId(postId);
            for (PostVote postVoteRep : postVoteListRep) {
                if (postVoteRep.getValue() > 0) {
                    postInfoDTO.increaseLikeCount();
                } else {
                    postInfoDTO.increaseDislikeCount();
                }
            }

            // CommentCount
            List<PostComment> postCommentListRep = postCommentRepository.findAllPostCommentByPostId(postId);
            postInfoDTO.setCommentCount(postCommentListRep.size());

            // Announce
            //TODO: Max size announce is 200-500 symbols
            int maxSizeAnnounce = 200;
            String announce = postRep.getText();
            if (announce.length() > maxSizeAnnounce) {
                announce = announce.substring(0, maxSizeAnnounce);
            }
            postInfoDTO.setAnnounce(announce);

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
            PostInfoDTO postInfoDTO = new PostInfoDTO();

            Post postRep = postListRep.get(i);
            postInfoDTO.setId(postRep.getId());
            postInfoDTO.setTitle(postRep.getTitle());
            postInfoDTO.setViewCount(postRep.getViewCount());

            UserSimple user = new UserSimple();
            user.setId(postRep.getUser().getId());
            user.setName(postRep.getUser().getName());
            postInfoDTO.setUser(user);

            String time = getStringTime(postRep.getTime());
            postInfoDTO.setTime(time);

            int postId = postRep.getId();
            // Likes/Dislikes
            List<PostVote> postVoteListRep = postVoteRepository.findAllPostVotesByPostId(postId);
            for (PostVote postVoteRep : postVoteListRep) {
                if (postVoteRep.getValue() > 0) {
                    postInfoDTO.increaseLikeCount();
                } else {
                    postInfoDTO.increaseDislikeCount();
                }
            }

            // CommentCount
            List<PostComment> postCommentListRep = postCommentRepository.findAllPostCommentByPostId(postId);
            postInfoDTO.setCommentCount(postCommentListRep.size());

            // Announce
            //TODO: Max size announce is 200-500 symbols
            int maxSizeAnnounce = 200;
            String announce = postRep.getText();
            if (announce.length() > maxSizeAnnounce) {
                announce = announce.substring(0, maxSizeAnnounce);
            }
            postInfoDTO.setAnnounce(announce);

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
