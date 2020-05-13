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
import main.model.responses.PostVoteDTO;
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
    public CollectionPostsResponseDTO getAllPosts(
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
        List<PostVoteDTO> posts = new ArrayList<>();
        for (int i = offset; i < minCountPostsOnPage + offset; i++) {
            if (i == allPostsCount) {
                break;
            }
            Post postRep = postListRep.get(i);
            UserSimple user = new UserSimple();
            user.setId(postRep.getUser().getId());
            user.setName(postRep.getUser().getName());

            PostVoteDTO postVoteDTO = new PostVoteDTO();
            postVoteDTO.setId(postRep.getId());
            postVoteDTO.setTitle(postRep.getTitle());
            postVoteDTO.setViewCount(postRep.getViewCount());
            postVoteDTO.setUser(user);

            String time = getStringTime(postRep.getTime());
            postVoteDTO.setTime(time);

            int postId = postRep.getId();
            // Likes/Dislikes
            List<PostVote> postVoteListRep = postVoteRepository.findAllPostVotesByPostId(postId);
            for (PostVote postVoteRep : postVoteListRep) {
                if (postVoteRep.getValue() > 0) {
                    postVoteDTO.increaseLikeCount();
                } else {
                    postVoteDTO.increaseDislikeCount();
                }
            }

            // CommentCount
            List<PostComment> postCommentListRep = postCommentRepository.findAllPostCommentByPostId(postId);
            for (PostComment postComment : postCommentListRep) {
                postVoteDTO.increaseCommentCount();
            }

            // Announce
            //TODO: Max size announce is 200-500 symbols
            int maxSizeAnnounce = 200;
            String announce = postRep.getText();
            if (announce.length() > maxSizeAnnounce) {
                announce = announce.substring(0, maxSizeAnnounce);
            }
            postVoteDTO.setAnnounce(announce);

            //===========================================================
            posts.add(postVoteDTO);
        }

        CollectionPostsResponseDTO collectionPostsResponseDTO = new CollectionPostsResponseDTO();
        collectionPostsResponseDTO.setCount(postRepository.findAll().size());
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
