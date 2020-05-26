package main.services;

import main.model.entities.Tag;

import java.util.List;

public interface TagService {
    Tag createTagIfNoExistsAndReturn(String tagName);

    List<Tag> findAllTagsByQuery(String query);

    List<Tag> findAll();
}
