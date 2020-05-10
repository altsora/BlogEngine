package main.controller;

import main.model.entities.Post;
import main.model.entities.PostVote;
import main.model.entities.User;
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
    public CollectionPostsResponseDTO method1(
            @RequestParam(value = "offset") long offset,
            @RequestParam(value = "limit") long limit,
            @RequestParam(value = "mode") String mode
    ) {

        List<PostVoteDTO> posts = new ArrayList<>();
        List<Post> postListRep = postRepository.findAll();
        for (int i = 0; i < limit; i++) {
            Post postRep = postListRep.get(i);
            UserSimple user = new UserSimple();
            user.setId(postRep.getUser().getId());
            user.setName(postRep.getUser().getName());

            PostVoteDTO postVoteDTO = new PostVoteDTO();
            postVoteDTO.setId(postRep.getId());
            postVoteDTO.setTime(postRep.getTime().toString());
            postVoteDTO.setTitle(postRep.getTitle());
            postVoteDTO.setViewCount(postRep.getViewCount());
            postVoteDTO.setUser(user);

            int postId = postRep.getId();
            List<PostVote> postVoteList = postVoteRepository.findAllPostVotesByPostId(postId);
            for (PostVote postVoteRep : postVoteList) {
                if (postVoteRep.getValue() > 0) {
                    postVoteDTO.increaseLikeCount();
                } else {
                    postVoteDTO.increaseDislikeCount();
                }
            }

            //TODO: Max size announce is 200-500 symbols
            int maxSizeAnnounce = 200;
            String announce = postRep.getText();
            if (announce.length() > maxSizeAnnounce) {
                announce = announce.substring(0, maxSizeAnnounce);
            }
            postVoteDTO.setAnnounce(announce);

            posts.add(postVoteDTO);
        }
//        for (Post postRep : postRepository.findAll()) {
//            UserSimple user = new UserSimple();
//            user.setId(postRep.getUser().getId());
//            user.setName(postRep.getUser().getName());
//
//            PostVoteDTO postVoteDTO = new PostVoteDTO();
//            postVoteDTO.setId(postRep.getId());
//            postVoteDTO.setTime(postRep.getTime().toString());
//            postVoteDTO.setTitle(postRep.getTitle());
//            postVoteDTO.setViewCount(postRep.getViewCount());
//            postVoteDTO.setUser(user);
//
//            int postId = postRep.getId();
//            List<PostVote> postVoteList = postVoteRepository.findAllPostVotesByPostId(postId);
//            for (PostVote postVoteRep : postVoteList) {
//                if (postVoteRep.getValue() > 0) {
//                    postVoteDTO.increaseLikeCount();
//                } else {
//                    postVoteDTO.increaseDislikeCount();
//                }
//            }
//
//            //TODO: Max size announce is 200-500 symbols
//            int maxSizeAnnounce = 200;
//            String announce = postRep.getText();
//            if (announce.length() > maxSizeAnnounce) {
//                announce = announce.substring(0, maxSizeAnnounce);
//            }
//            postVoteDTO.setAnnounce(announce);
//
//            posts.add(postVoteDTO);
//        }
        CollectionPostsResponseDTO collectionPostsResponseDTO = new CollectionPostsResponseDTO();
        collectionPostsResponseDTO.setCount(postRepository.findAll().size());
        collectionPostsResponseDTO.setPosts(posts);

        return collectionPostsResponseDTO;
    }


    @GetMapping(value = "/api/tag")
    public ResponseEntity getTag() {
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
