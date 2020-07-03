package main.services.impl;

import lombok.RequiredArgsConstructor;
import main.model.entities.Post;
import main.model.entities.User;
import main.model.enums.ActivesType;
import main.model.enums.ModerationStatus;
import main.repositories.PostRepository;
import main.services.PostService;
import main.services.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserService userService;

    //==================================================================================================================

    @Override
    public List<Post> findAllPostPopular(ActivesType activesType, ModerationStatus moderationStatus, int offset, int limit) {
        int pageNumber = offset / limit;
        Pageable sortedByCountComment = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.DESC, PostRepository.COUNT_COMMENTS));
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.findAllPostPopular(isActive, moderationStatus, sortedByCountComment);
    }

    @Override
    public List<Post> findAllHiddenPostsByUserId(int offset, int limit, long userId) {
        int pageNumber = offset / limit;
        Pageable sortedByPostTime = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.DESC, PostRepository.POST_TIME));
        byte isActive = (byte) 0;
        return postRepository.findAllHiddenPostsByUserId(isActive, userId, sortedByPostTime);
    }

    @Override
    public List<Post> findAllPostsByUserId(ActivesType activesType, ModerationStatus moderationStatus, int offset, int limit, long userId) {
        int pageNumber = offset / limit;
        Pageable sortedByPostTime = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.DESC, PostRepository.POST_TIME));
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.findAllPostsByUserId(isActive, moderationStatus, userId, sortedByPostTime);
    }

    @Override
    public List<Post> findAllPostBest(ActivesType activesType, ModerationStatus moderationStatus, int offset, int limit) {
        int pageNumber = offset / limit;
        Pageable sortedByCountLikes = PageRequest.of(pageNumber, limit, Sort.by(PostRepository.COUNT_LIKES).descending());
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.findAllPostBest(isActive, moderationStatus, sortedByCountLikes);
    }

    @Override
    public List<Post> findAllPostSortedByDate(ActivesType activesType, ModerationStatus moderationStatus, int offset, int limit, Sort.Direction direction) {
        int pageNumber = offset / limit;
        Pageable sortedByPostTime = PageRequest.of(pageNumber, limit, Sort.by(direction, PostRepository.POST_TIME));
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.findAllPostSortedByDate(isActive, moderationStatus, sortedByPostTime);
    }

    @Override
    public List<Post> findAllNewPosts(ActivesType activesType, int offset, int limit) {
        int pageNumber = offset / limit;
        Pageable sortedByPostTime = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.DESC, PostRepository.POST_TIME));
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.findAllPosts(isActive, ModerationStatus.NEW, sortedByPostTime);
    }

    @Override
    public List<Post> findAllPostsByModeratorId(ActivesType activesType, ModerationStatus moderationStatus, int offset, int limit, long moderatorId) {
        int pageNumber = offset / limit;
        Pageable sortedByPostTime = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.DESC, PostRepository.POST_TIME));
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.findAllPostsByModeratorId(isActive, moderationStatus, moderatorId, sortedByPostTime);
    }

    @Override
    public List<Post> findAllPostByQuery(ActivesType activesType, ModerationStatus moderationStatus, int offset, int limit, String query) {
        int pageNumber = offset / limit;
        Pageable sortedByPostTime = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.ASC, PostRepository.POST_TIME));
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.findAllPostByQuery(isActive, moderationStatus, query, sortedByPostTime);
    }

    @Override
    public List<Post> findAllPostByDate(ActivesType activesType, ModerationStatus moderationStatus, int offset, int limit, String date) {
        int pageNumber = offset / limit;
        String[] var = date.split("-");
        int year = Integer.parseInt(var[0]);
        int month = Integer.parseInt(var[1]);
        int dayOfMonth = Integer.parseInt(var[2]);
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        Pageable sortedByPostTime = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.ASC, PostRepository.POST_TIME));
        return postRepository.findAllPostByDate(isActive, moderationStatus, year, month, dayOfMonth, sortedByPostTime);
    }

    @Override
    public List<Post> findAllPostByTag(ActivesType activesType, ModerationStatus moderationStatus, int offset, int limit, String tag) {
        int pageNumber = offset / limit;
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        Pageable sortedByPostTime = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.ASC, PostRepository.POST_TIME));
        return postRepository.findAllPostByTag(isActive, moderationStatus, tag, sortedByPostTime);
    }

    @Override
    public List<Integer> findAllYearsOfPublication(ActivesType activesType, ModerationStatus moderationStatus) {
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        Sort sort = Sort.by(Sort.Direction.DESC, PostRepository.POST_TIME);
        return postRepository.findAllYearsOfPublication(isActive, moderationStatus, sort);
    }

    @Override
    public Map<String, Long> getDateAndCountPosts(ActivesType activesType, ModerationStatus moderationStatus, int year) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd");
        Map<String, Long> result = new HashMap<>();
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        List<Tuple> datesAndCountPosts = postRepository.getDateAndCountPosts(isActive, moderationStatus, year, Sort.by(Sort.Direction.ASC, PostRepository.POST_TIME));
        for (Tuple tuple : datesAndCountPosts) {
            Object[] pair = tuple.toArray();
            try {
                LocalDateTime localTime = (LocalDateTime) pair[0];

                ZonedDateTime localZone = localTime.atZone(ZoneId.systemDefault());
                ZonedDateTime utcZone = localZone.withZoneSameInstant(ZoneId.of("UTC"));
                LocalDateTime utcTime = utcZone.toLocalDateTime();

                String date = formatter.format(utcTime);
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
        return postRepository.getTotalCountOfNewPosts(isActive, ModerationStatus.NEW);
    }

    @Override
    public int getTotalCountOfPostsByDate(ActivesType activesType, ModerationStatus moderationStatus, String date) {
        String[] var = date.split("-");
        int year = Integer.parseInt(var[0]);
        int month = Integer.parseInt(var[1]);
        int dayOfMonth = Integer.parseInt(var[2]);
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.getTotalCountOfPostsByDate(isActive, moderationStatus, year, month, dayOfMonth);
    }

    @Override
    public int getTotalCountOfPostsByModeratorId(ActivesType activesType, ModerationStatus moderationStatus, long moderatorId) {
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.getTotalCountOfPostsByModeratorId(isActive, moderationStatus, moderatorId);
    }

    @Override
    public int getTotalCountView(ActivesType activesType, ModerationStatus moderationStatus) {
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.getTotalCountView(isActive, moderationStatus);
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
    public int getTotalCountOfPostsByUserId(ActivesType activesType, ModerationStatus moderationStatus, long userId) {
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.getTotalCountOfPostsByUserId(isActive, moderationStatus, userId);
    }

    @Override
    public int getTotalCountOfHiddenPostsByUserId(long userId) {
        byte isActive = 0;
        return postRepository.getTotalCountOfHiddenPostsByUserId(isActive, userId);
    }

    @Override
    public int getTotalCountOfPosts(ActivesType activesType, ModerationStatus moderationStatus) {
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.getTotalCountOfPosts(isActive, moderationStatus);
    }

    @Override
    public int getTotalCountOfPostsByQuery(ActivesType activesType, ModerationStatus moderationStatus, String query) {
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.getTotalCountOfPostsByQuery(isActive, moderationStatus, query);
    }

    @Override
    public int getTotalCountOfPostsByTag(ActivesType activesType, ModerationStatus moderationStatus, String tag) {
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        return postRepository.getTotalCountOfPostsByTag(isActive, moderationStatus, tag);
    }

    @Override
    public Post findById(long postId) {
        return postRepository.findById(postId).orElse(null);
    }

    @Override
    public Post updateViewCount(Post post) {
        post.setViewCount(post.getViewCount() + 1);
        return postRepository.saveAndFlush(post);
    }

    @Override
    public LocalDateTime getDateOfTheEarliestPost(ActivesType activesType, ModerationStatus moderationStatus) {
        byte isActive = activesType == ActivesType.ACTIVE ? (byte) 1 : 0;
        LocalDateTime localDateTime = postRepository.getDateOfTheEarliestPost(isActive, moderationStatus);
        if (localDateTime != null) {
            ZonedDateTime localZone = localDateTime.atZone(ZoneId.systemDefault());
            ZonedDateTime utcZone = localZone.withZoneSameInstant(ZoneId.of("UTC"));
            localDateTime = utcZone.toLocalDateTime();
        }
        return localDateTime;
    }

    @Override
    public LocalDateTime getDateOfTheEarliestPostByUserId(long userId) {
        LocalDateTime localDateTime = postRepository.getDateOfTheEarliestPostByUserId(userId);
        if (localDateTime != null) {
            ZonedDateTime localZone = localDateTime.atZone(ZoneId.systemDefault());
            ZonedDateTime utcZone = localZone.withZoneSameInstant(ZoneId.of("UTC"));
            localDateTime = utcZone.toLocalDateTime();
        }
        return localDateTime;
    }

    @Override
    public Post addPost(byte isActive, User user, LocalDateTime postTime, String postTitle, String postText) {
        Post post = new Post();
        post.setIsActive(isActive);
        post.setUser(user);
        post.setTime(postTime);
        post.setTitle(postTitle);
        post.setText(postText);
        return postRepository.saveAndFlush(post);
    }

    @Override
    public void setModerationStatus(long userId, long postId, ModerationStatus moderationStatus) {
        User moderator = userService.findById(userId);
        Post post = postRepository.findById(postId).orElseThrow();
        post.setModerationStatus(moderationStatus);
        post.setModerator(moderator);
        postRepository.saveAndFlush(post);
    }
}
