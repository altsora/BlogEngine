package main.controller;

import main.model.entities.Post;
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
        CollectionPostsResponseDTO collectionPostsResponseDTO = new CollectionPostsResponseDTO();
        collectionPostsResponseDTO.setCount(postRepository.findAll().size());

        List<PostVoteDTO> posts = new ArrayList<>();
        for (Post postRep : postRepository.findAll()) {
            UserSimple user = new UserSimple();
            user.setId(postRep.getUser().getId());
            user.setName(postRep.getUser().getName());

            PostVoteDTO postVoteDTO = new PostVoteDTO();
            postVoteDTO.setId(postRep.getId());
            postVoteDTO.setTime(postRep.getTime().toString());
            postVoteDTO.setTitle(postRep.getTitle());
            postVoteDTO.setAnnounce("Анонс");
            postVoteDTO.setViewCount(postRep.getViewCount());
            postVoteDTO.setUser(user);

            posts.add(postVoteDTO);
        }
        collectionPostsResponseDTO.setPosts(posts);

        return collectionPostsResponseDTO;
    }

    @GetMapping(value = "/api/tag")
    public ResponseEntity getTag() {
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
