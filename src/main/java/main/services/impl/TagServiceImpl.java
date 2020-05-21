package main.services.impl;

import main.model.entities.Tag;
import main.repositories.TagRepository;
import main.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private TagRepository tagRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Tag> findAllTagsByQuery(String query) {
        return tagRepository.findAllTagsByQuery(query);
    }

    @Override
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }
}
