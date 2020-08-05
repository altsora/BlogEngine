package main.services;

import main.model.entity.Post;
import main.model.entity.Tag;
import main.model.entity.Tag2Post;

import java.util.List;

public interface Tag2PostService {
    void addTag2Post(Post post, Tag tag);

    void updateTagsByPostId(long postId, List<String> newTags);

    List<Tag2Post> findAllTag2PostByPostId(long postId);
}
