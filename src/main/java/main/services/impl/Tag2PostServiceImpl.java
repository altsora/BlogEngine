package main.services.impl;

import main.model.entities.Tag2Post;
import main.repositories.Tag2PostRepository;
import main.services.Tag2PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Tag2PostServiceImpl implements Tag2PostService {
    private Tag2PostRepository tag2PostRepository;

    @Autowired
    public Tag2PostServiceImpl(Tag2PostRepository tag2PostRepository) {
        this.tag2PostRepository = tag2PostRepository;
    }

    @Override
    public List<Tag2Post> findAllTag2PostByPostId(int postId) {
        return tag2PostRepository.findAllTag2PostByPostId(postId);
    }
}
