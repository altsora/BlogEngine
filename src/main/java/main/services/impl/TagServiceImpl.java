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

    //==================================================================================================================

    @Override
    public Tag createTagIfNoExistsAndReturn(String tagName) {
        Tag tag = tagRepository.existsByTag(tagName);
        if (tag == null) {
            tag = new Tag();
            tag.setName(tagName);
            tag = tagRepository.saveAndFlush(tag);
        }
        return tag;
    }

    @Override
    public Tag findByName(String tagName) {
        return tagRepository.existsByTag(tagName);
    }

    @Override
    public void removeByTagName(String tagName) {
        Tag tag = tagRepository.existsByTag(tagName);
        if (tag != null) tagRepository.deleteById(tag.getId());
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
