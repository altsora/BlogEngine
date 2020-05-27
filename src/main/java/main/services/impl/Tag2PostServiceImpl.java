package main.services.impl;

import main.model.entities.Post;
import main.model.entities.Tag;
import main.model.entities.Tag2Post;
import main.repositories.PostRepository;
import main.repositories.Tag2PostRepository;
import main.services.PostService;
import main.services.Tag2PostService;
import main.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Tag2PostServiceImpl implements Tag2PostService {
    private Tag2PostRepository tag2PostRepository;
    private PostService postService;
    private TagService tagService;

    @Autowired
    public Tag2PostServiceImpl(Tag2PostRepository tag2PostRepository, PostService postService, TagService tagService) {
        this.tag2PostRepository = tag2PostRepository;
        this.postService = postService;
        this.tagService = tagService;
    }

    //==================================================================================================================

    @Override
    public void addTag2Post(Tag2Post tag2Post) {
        tag2PostRepository.saveAndFlush(tag2Post);
    }

    @Override
    public void updateTagsByPostId(long postId, List<String> newTags) {
        List<Tag2Post> tag2Posts = tag2PostRepository.findAllTag2PostByPostId(postId);
        for (Tag2Post tag2Post : tag2Posts) {
            String tagName = tag2Post.getTag().getName();
            if (!newTags.contains(tagName)) {
                tag2PostRepository.deleteById(tag2Post.getId());
            }
            if (tag2PostRepository.getCountPostsByTag(tagName) == 0) {
                tagService.removeByTagName(tagName);
            }
        }

        for (String newTag : newTags) {
            if (tag2PostRepository.existsByPostIdAndTagName(postId, newTag) == null) {
                Post post = postService.findById(postId);
                Tag tag = tagService.findByName(newTag);
                Tag2Post tag2Post = new Tag2Post();
                tag2Post.setPost(post);
                tag2Post.setTag(tag);
                addTag2Post(tag2Post);
            }
        }
    }

    @Override
    public List<Tag2Post> findAllTag2PostByPostId(long postId) {
        return tag2PostRepository.findAllTag2PostByPostId(postId);
    }
}
