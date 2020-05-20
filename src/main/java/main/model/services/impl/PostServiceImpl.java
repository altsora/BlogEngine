package main.model.services.impl;

import main.model.entities.Post;
import main.model.entities.enums.ActivesType;
import main.model.entities.enums.ModerationStatusType;
import main.model.repositories.PostRepository;
import main.model.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Override
    public List<Post> findAllPostPopular(ActivesType activesType, ModerationStatusType moderationStatusType, int offset, int limit) {
        int pageNumber = offset / limit;
        Pageable sortedByCountComment = PageRequest.of(pageNumber, limit, Sort.by(PostRepository.COUNT_COMMENTS).descending());
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.findAllPostPopular(isActive, moderationStatusType, sortedByCountComment);
    }

    @Override
    public List<Post> findAllPostBest(ActivesType activesType, ModerationStatusType moderationStatusType, int offset, int limit) {
        int pageNumber = offset / limit;
        Pageable sortedByCountLikes = PageRequest.of(pageNumber, limit, Sort.by(PostRepository.COUNT_LIKES).descending());
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.findAllPostBest(isActive, moderationStatusType, sortedByCountLikes);
    }

    @Override
    public List<Post> findAllPostSortedByDate(ActivesType activesType, ModerationStatusType moderationStatusType,
                                              int offset, int limit, Sort.Direction direction) {
        int pageNumber = offset / limit;
        Pageable sortedByPostTime = PageRequest.of(pageNumber, limit, Sort.by(direction, PostRepository.POST_TIME));
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.findAllPostSortedByDate(isActive, moderationStatusType, sortedByPostTime);
    }

    @Override
    public List<Post> findAllPostByQuery(ActivesType activesType, ModerationStatusType moderationStatusType,
                                         int offset, int limit, String query) {
        int pageNumber = offset / limit;
        Pageable sortedByPostTime = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.ASC, PostRepository.POST_TIME));
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.findAllPostByQuery(isActive, moderationStatusType, query, sortedByPostTime);
    }

    @Override
    public List<Post> findAllPostByDate(ActivesType activesType, ModerationStatusType moderationStatusType,
                                        int offset, int limit, String date) {
        int pageNumber = offset / limit;
        String[] var = date.split("-");
        int year = Integer.parseInt(var[0]);
        int month = Integer.parseInt(var[1]);
        int dayOfMonth = Integer.parseInt(var[2]);
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        Pageable sortedByPostTime = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.ASC, PostRepository.POST_TIME));
        return postRepository.findAllPostByDate(isActive, moderationStatusType, year, month, dayOfMonth, sortedByPostTime);
    }

    @Override
    public List<Post> findAllPostByTag(ActivesType activesType, ModerationStatusType moderationStatusType, int offset, int limit, String tag) {
        int pageNumber = offset / limit;
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        Pageable sortedByPostTime = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.ASC, PostRepository.POST_TIME));
        return postRepository.findAllPostByTag(isActive, moderationStatusType, tag, sortedByPostTime);
    }

    @Override
    public int getTotalNumberOfPostsByDate(ActivesType activesType, ModerationStatusType moderationStatusType, String date) {
        String[] var = date.split("-");
        int year = Integer.parseInt(var[0]);
        int month = Integer.parseInt(var[1]);
        int dayOfMonth = Integer.parseInt(var[2]);
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.getTotalNumberOfPostsByDate(isActive, moderationStatusType, year, month, dayOfMonth);
    }


    @Override
    public int getTotalNumberOfPosts(ActivesType activesType, ModerationStatusType moderationStatusType) {
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.getTotalNumberOfPosts(isActive, moderationStatusType);
    }

    @Override
    public int getTotalNumberOfPostsByQuery(ActivesType activesType, ModerationStatusType moderationStatusType, String query) {
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.getTotalNumberOfPostsByQuery(isActive, moderationStatusType, query);
    }

    @Override
    public int getTotalNumberOfPostsByTag(ActivesType activesType, ModerationStatusType moderationStatusType, String tag) {
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.getTotalNumberOfPostsByTag(isActive, moderationStatusType, tag);
    }



    @Override
    public Post findPostById(int postId, ActivesType activesType, ModerationStatusType moderationStatusType) {
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.findPostById(postId, isActive, moderationStatusType);
    }


}
