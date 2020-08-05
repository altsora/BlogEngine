package main.services.impl;

import lombok.RequiredArgsConstructor;
import main.model.entity.Post;
import main.model.entity.Tag;
import main.model.entity.Tag2Post;
import main.repositories.Tag2PostRepository;
import main.services.PostService;
import main.services.Tag2PostService;
import main.services.TagService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class Tag2PostServiceImpl implements Tag2PostService {
    private final Tag2PostRepository tag2PostRepository;
    private final PostService postService;
    private final TagService tagService;

    //==================================================================================================================

    @Override
    public void addTag2Post(Post post, Tag tag) {
        Tag2Post tag2Post = new Tag2Post();
        tag2Post.setPost(post);
        tag2Post.setTag(tag);
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
                addTag2Post(post, tag);
            }
        }
    }

    @Override
    public List<Tag2Post> findAllTag2PostByPostId(long postId) {
        return tag2PostRepository.findAllTag2PostByPostId(postId);
    }
}
