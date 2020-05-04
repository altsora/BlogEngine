package main.controller;

import main.model.entities.Post;
import main.model.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ApiAuthController {

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/api/post/")
    public ResponseEntity getAllPosts() {
        List<Post> postList = postRepository.findAll();
        return new ResponseEntity(postList, HttpStatus.OK);
    }
}