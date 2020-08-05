package main.services;

import main.model.entity.Tag;

import java.util.List;

public interface TagService {
    void removeByTagName(String tagName);

    List<String> getTagsByPostId(long postId);

    List<Tag> findAll();

    List<Tag> findAllTagsByQuery(String query);

    Tag createTagIfNoExistsAndReturn(String tagName);

    Tag findByName(String tagName);
}