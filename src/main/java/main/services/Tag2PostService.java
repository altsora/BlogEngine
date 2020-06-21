package main.services;

import main.model.entities.Post;
import main.model.entities.Tag;
import main.model.entities.Tag2Post;

import java.util.List;

public interface Tag2PostService {
    void addTag2Post(Post post, Tag tag);

    void updateTagsByPostId(long postId, List<String> newTags);

    List<Tag2Post> findAllTag2PostByPostId(long postId);
}
