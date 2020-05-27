package main.services.impl;

import main.model.entities.Post;
import main.model.entities.PostVote;
import main.model.entities.User;
import main.model.entities.enums.ActivesType;
import main.model.entities.enums.ModerationStatusType;
import main.repositories.PostRepository;
import main.services.PostService;
import main.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {
    private PostRepository postRepository;
    private UserService userService;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    //==================================================================================================================

    @Override
    public List<Post> findAllPostPopular(ActivesType activesType, ModerationStatusType moderationStatusType, int offset, int limit) {
        int pageNumber = offset / limit;
        Pageable sortedByCountComment = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.DESC, PostRepository.COUNT_COMMENTS));
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.findAllPostPopular(isActive, moderationStatusType, sortedByCountComment);
    }

    @Override
    public List<Post> findAllHiddenPostsByUserId(int offset, int limit, long userId) {
        int pageNumber = offset / limit;
        Pageable sortedByPostTime = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.DESC, PostRepository.POST_TIME));
        byte isActive = (byte) 0;
        return postRepository.findAllHiddenPostsByUserId(isActive, userId, sortedByPostTime);
    }

    @Override
    public List<Post> findAllPostsByUserId(ActivesType activesType, ModerationStatusType moderationStatusType, int offset, int limit, long userId) {
        int pageNumber = offset / limit;
        Pageable sortedByPostTime = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.DESC, PostRepository.POST_TIME));
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.findAllPostsByUserId(isActive, moderationStatusType, userId, sortedByPostTime);
    }

    @Override
    public List<Post> findAllPostBest(ActivesType activesType, ModerationStatusType moderationStatusType, int offset, int limit) {
        int pageNumber = offset / limit;
        Pageable sortedByCountLikes = PageRequest.of(pageNumber, limit, Sort.by(PostRepository.COUNT_LIKES).descending());
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.findAllPostBest(isActive, moderationStatusType, sortedByCountLikes);
    }

    @Override
    public List<Post> findAllPostSortedByDate(ActivesType activesType, ModerationStatusType moderationStatusType, int offset, int limit, Sort.Direction direction) {
        int pageNumber = offset / limit;
        Pageable sortedByPostTime = PageRequest.of(pageNumber, limit, Sort.by(direction, PostRepository.POST_TIME));
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.findAllPostSortedByDate(isActive, moderationStatusType, sortedByPostTime);
    }

    @Override
    public List<Post> findAllNewPosts(ActivesType activesType, int offset, int limit) {
        int pageNumber = offset / limit;
        Pageable sortedByPostTime = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.ASC, PostRepository.POST_TIME));
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.findAllPosts(isActive, ModerationStatusType.NEW, sortedByPostTime);
    }

    @Override
    public List<Post> findAllPostsByModeratorId(ActivesType activesType, ModerationStatusType moderationStatusType, int offset, int limit, long moderatorId) {
        int pageNumber = offset / limit;
        Pageable sortedByPostTime = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.ASC, PostRepository.POST_TIME));
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.findAllPostsByModeratorId(isActive, moderationStatusType, moderatorId, sortedByPostTime);
    }

    @Override
    public List<Post> findAllPostByQuery(ActivesType activesType, ModerationStatusType moderationStatusType, int offset, int limit, String query) {
        int pageNumber = offset / limit;
        Pageable sortedByPostTime = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.ASC, PostRepository.POST_TIME));
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.findAllPostByQuery(isActive, moderationStatusType, query, sortedByPostTime);
    }

    @Override
    public List<Post> findAllPostByDate(ActivesType activesType, ModerationStatusType moderationStatusType, int offset, int limit, String date) {
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
    public List<Integer> findAllYearsOfPublication(ActivesType activesType, ModerationStatusType moderationStatusType) {
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        Sort sort = Sort.by(Sort.Direction.DESC, PostRepository.POST_TIME);
        return postRepository.findAllYearsOfPublication(isActive, moderationStatusType, sort);
    }

    @Override
    public Map<String, Long> getDateAndCountPosts(ActivesType activesType, ModerationStatusType moderationStatusType, int year) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd");
        Map<String, Long> result = new HashMap<>();
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        List<Tuple> datesAndCountPosts = postRepository.getDateAndCountPosts(isActive, moderationStatusType, year, Sort.by(Sort.Direction.ASC, PostRepository.POST_TIME));
        for (Tuple tuple : datesAndCountPosts) {
            Object[] pair = tuple.toArray();
            try {
                LocalDateTime localDateTime = (LocalDateTime) pair[0];
                String date = formatter.format(localDateTime);
                Long count = (Long) pair[1];
                result.put(date, count);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public int getTotalCountOfNewPosts(ActivesType activesType) {
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.getTotalCountOfNewPosts(isActive, ModerationStatusType.NEW);
    }

    @Override
    public int getTotalCountOfPostsByDate(ActivesType activesType, ModerationStatusType moderationStatusType, String date) {
        String[] var = date.split("-");
        int year = Integer.parseInt(var[0]);
        int month = Integer.parseInt(var[1]);
        int dayOfMonth = Integer.parseInt(var[2]);
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.getTotalCountOfPostsByDate(isActive, moderationStatusType, year, month, dayOfMonth);
    }

    @Override
    public int getTotalCountOfPostsByModeratorId(ActivesType activesType, ModerationStatusType moderationStatusType, long moderatorId) {
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.getTotalCountOfPostsByModeratorId(isActive, moderationStatusType, moderatorId);
    }

    @Override
    public int getTotalCountView(ActivesType activesType, ModerationStatusType moderationStatusType) {
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.getTotalCountView(isActive, moderationStatusType);
    }

    @Override
    public int getTotalCountViewByUserId(long userId) {
        return postRepository.getTotalCountViewByUserId(userId);
    }

    @Override
    public int getTotalCountOfPostsByUserId(long userId) {
        return postRepository.getTotalCountOfPostsByUserId(userId);
    }

    @Override
    public int getTotalCountOfPostsByUserId(ActivesType activesType, ModerationStatusType moderationStatusType, long userId) {
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.getTotalCountOfPostsByUserId(isActive, moderationStatusType, userId);
    }

    @Override
    public int getTotalCountOfHiddenPostsByUserId(long userId) {
        byte isActive = 0;
        return postRepository.getTotalCountOfHiddenPostsByUserId(isActive, userId);
    }

    @Override
    public int getTotalCountOfPosts(ActivesType activesType, ModerationStatusType moderationStatusType) {
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.getTotalCountOfPosts(isActive, moderationStatusType);
    }

    @Override
    public int getTotalCountOfPostsByQuery(ActivesType activesType, ModerationStatusType moderationStatusType, String query) {
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.getTotalCountOfPostsByQuery(isActive, moderationStatusType, query);
    }

    @Override
    public int getTotalCountOfPostsByTag(ActivesType activesType, ModerationStatusType moderationStatusType, String tag) {
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.getTotalCountOfPostsByTag(isActive, moderationStatusType, tag);
    }

    @Override
    public Post findPostByIdWithCondition(long postId, ActivesType activesType, ModerationStatusType moderationStatusType) {
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.findPostByPostId(postId, isActive, moderationStatusType);
    }

    @Override
    public Post findById(long postId) {
        return postRepository.findById(postId).orElse(null);
    }

    @Override
    public LocalDateTime getDateOfTheEarliestPost(ActivesType activesType, ModerationStatusType moderationStatusType) {
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.getDateOfTheEarliestPost(isActive, moderationStatusType);
    }

    @Override
    public LocalDateTime getDateOfTheEarliestPostByUserId(long userId) {
        return postRepository.getDateOfTheEarliestPostByUserId(userId);
    }

    @Override
    public Post addPostAndReturn(Post post) {
        return postRepository.saveAndFlush(post);
    }

    @Override
    public void setModerationStatus(long userId, long postId, ModerationStatusType moderationStatusType) {
        User moderator = userService.findById(userId);
        Post post = postRepository.findById(postId).orElseThrow();
        post.setModerationStatus(moderationStatusType);
        post.setModerator(moderator);
        postRepository.saveAndFlush(post);
    }
}
