package main.service;

import main.model.entity.Tag;

import java.util.List;

public interface TagService {
    Tag createTagIfNoExistsAndReturn(String tagName);

    Tag findByName(String tagName);

    void removeByTagName(String tagName);

    List<Tag> findAllTagsByQuery(String query);

    List<Tag> findAll();
}
