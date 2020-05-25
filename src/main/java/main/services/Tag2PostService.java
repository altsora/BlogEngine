package main.services;

import main.model.entities.Tag2Post;

import java.util.List;

public interface Tag2PostService {

    List<Tag2Post> findAllTag2PostByPostId(long postId);
}
