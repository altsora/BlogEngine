package main.model.services;

import main.model.entities.Tag;

import java.util.List;

public interface TagService {

    List<Tag> findAllTagsByQuery(String query);
    List<Tag> findAll();
}
