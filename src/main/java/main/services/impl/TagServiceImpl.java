package main.services.impl;

import main.model.entities.Tag;
import main.model.entities.Tag2Post;
import main.repositories.TagRepository;
import main.services.Tag2PostService;
import main.services.TagService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final Tag2PostService tag2PostService;

    public TagServiceImpl(
            @Lazy TagRepository tagRepository,
            @Lazy Tag2PostService tag2PostService) {
        this.tagRepository = tagRepository;
        this.tag2PostService = tag2PostService;
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

    @Override
    public List<String> getTagsByPostId(long postId) {
        List<Tag2Post> tag2PostListRep = tag2PostService.findAllTag2PostByPostId(postId);
        List<String> tags = new ArrayList<>();
        for (Tag2Post tag2PostRep : tag2PostListRep) {
            tags.add(tag2PostRep.getTag().getName());
        }
        return tags;
    }
}
