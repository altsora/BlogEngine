package main.service.impl;

import lombok.RequiredArgsConstructor;
import main.model.entity.Tag;
import main.repository.TagRepository;
import main.service.TagService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

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
