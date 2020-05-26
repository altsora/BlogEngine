package main.services;

import main.model.entities.Tag2Post;

import java.util.List;

public interface Tag2PostService {
    void addTag2Post(Tag2Post tag2Post);

    List<Tag2Post> findAllTag2PostByPostId(long postId);
}
