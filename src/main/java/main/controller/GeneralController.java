package main.controller;

import lombok.RequiredArgsConstructor;
import main.api.requests.ModerationForm;
import main.api.requests.NewCommentForm;
import main.api.requests.SettingsForm;
import main.api.requests.UpdateProfileForm;
import main.api.responses.*;
import main.model.entities.*;
import main.services.*;
import main.utils.ImageUtil;
import main.utils.TimeUtil;
import org.jsoup.Jsoup;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.model.enums.ActivityStatus.ACTIVE;
import static main.model.enums.ModerationStatus.ACCEPTED;
import static main.model.enums.ModerationStatus.DECLINED;
import static main.model.enums.SettingsCode.*;
import static main.model.enums.SettingsValue.YES;
import static main.utils.MessageUtil.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class GeneralController {
    private final AuthService authService;
    private final BlogResponse blog;
    private final GlobalSettingsService globalSettingsService;
    private final PostCommentService postCommentService;
    private final PostService postService;
    private final PostVoteService postVoteService;
    private final TagService tagService;
    private final UserService userService;

    //==================================================================================================================

    @GetMapping(value = "/init")
    public AbstractResponse init() {
        return blog;
    }

    @GetMapping(value = "/settings")
    public Map<String, Boolean> getSettings() {
        Map<String, Boolean> settings = new HashMap<>();
        List<GlobalSetting> globalSettings = globalSettingsService.findAll();
        for (GlobalSetting setting : globalSettings) {
            String code = setting.getCode().name();
            boolean enable = setting.getValue() == YES;
            settings.put(code, enable);
        }
        return settings;
    }

    @PutMapping(value = "/settings")
    public ResponseEntity<String> saveSettings(@RequestBody SettingsForm settingsForm) {
        if (authService.isUserAuthorize()) {
            User user = userService.findById(authService.getAuthorizedUserId());
            if (user.isModerator()) {
                boolean multiUserModeValue = settingsForm.isMultiUserModeValue();
                boolean postPreModerationValue = settingsForm.isPostPreModerationValue();
                boolean statisticsIsPublicValue = settingsForm.isStatisticsIsPublicValue();

                globalSettingsService.setValue(MULTIUSER_MODE, multiUserModeValue);
                globalSettingsService.setValue(POST_PREMODERATION, postPreModerationValue);
                globalSettingsService.setValue(STATISTICS_IS_PUBLIC, statisticsIsPublicValue);
                return ResponseEntity.ok().body("Settings saved successfully");
            }
        }
        return ResponseEntity.ok().body("Changes were not saved");
    }

    @GetMapping(value = "/calendar")
    public ResponseEntity<AbstractResponse> getCalendar(@RequestParam(value = "year", required = false) Integer year) {
        if (year == null) {
            year = LocalDateTime.now(TimeUtil.TIME_ZONE).getYear();
        }
        List<Integer> years = postService.findAllYearsOfPublication(ACTIVE, ACCEPTED);
        Map<String, Long> posts = postService.getDateAndCountPosts(ACTIVE, ACCEPTED, year);
        CalendarResponse calendar = CalendarResponse.builder().years(years).posts(posts).build();
        return ResponseEntity.ok(calendar);
    }

    @GetMapping(value = "/statistics/all")
    public ResponseEntity<AbstractResponse> getBlogStatistics() {
        if (globalSettingsService.settingStatisticsIsPublicIsEnabled()) {
            int dislikesCount = postVoteService.getTotalCountDislikes();
            int likesCount = postVoteService.getTotalCountLikes();
            int postsCount = postService.getTotalCountOfPosts(ACTIVE, ACCEPTED);
            int viewsCount = postService.getTotalCountView(ACTIVE, ACCEPTED);
            LocalDateTime localDateTime = postService.getDateOfTheEarliestPost(ACTIVE, ACCEPTED);
            long firstPublication = localDateTime == null ?
                    0L : TimeUtil.getTimestampFromLocalDateTime(localDateTime);

            StatisticResponse statistic = StatisticResponse.builder()
                    .dislikesCount(dislikesCount)
                    .firstPublication(firstPublication)
                    .likesCount(likesCount)
                    .postsCount(postsCount)
                    .viewsCount(viewsCount)
                    .build();

            return ResponseEntity.ok(statistic);
        }
        if (authService.isUserAuthorize()) {
            return new ResponseEntity<>(getMyStatistics().getBody(), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }

    @GetMapping(value = "/statistics/my")
    public ResponseEntity<AbstractResponse> getMyStatistics() {
        long userId = authService.getAuthorizedUserId();
        int likesCount = postVoteService.getTotalCountLikesByUserId(userId);
        int dislikesCount = postVoteService.getTotalCountDislikesByUserId(userId);
        int postsCount = postService.getTotalCountOfPostsByUserId(userId);
        int viewsCount = postService.getTotalCountViewByUserId(userId);
        LocalDateTime localDateTime = postService.getDateOfTheEarliestPostByUserId(userId);
        long firstPublication = localDateTime == null ?
                0L : TimeUtil.getTimestampFromLocalDateTime(localDateTime);

        StatisticResponse statistic = StatisticResponse.builder()
                .dislikesCount(dislikesCount)
                .firstPublication(firstPublication)
                .likesCount(likesCount)
                .postsCount(postsCount)
                .viewsCount(viewsCount)
                .build();

        return ResponseEntity.ok(statistic);
    }

    @GetMapping(value = "/tag")
    public ResponseEntity<AbstractResponse> getTagList(@RequestParam(value = "query", required = false) String query) {
        double minNormalizedWeight = 0.3d;
        List<Tag> tagListRep = (query == null || query.equals("")) ?
                tagService.findAll() :
                tagService.findAllTagsByQuery(query);
        List<Double> weights = new ArrayList<>();
        int totalNumberOfPosts = postService.getTotalCountOfPosts(ACTIVE, ACCEPTED);
        double maxWeight = -1;
        double weight;
        int countPosts;
        for (Tag tagRep : tagListRep) {
            countPosts = postService.getTotalCountOfPostsByTag(ACTIVE, ACCEPTED, tagRep.getName());
            weight = (double) countPosts / totalNumberOfPosts;
            weights.add(weight);
            if (weight > maxWeight) {
                maxWeight = weight;
            }
        }

        List<TagResponse> tags = new ArrayList<>();
        int size = tagListRep.size();
        for (int i = 0; i < size; i++) {
            String tagName = tagListRep.get(i).getName();
            double normalizedWeight = weights.get(i) / maxWeight;
            if (Double.compare(normalizedWeight, minNormalizedWeight) >= 0) {
                TagResponse tag = TagResponse.builder()
                        .name(tagName)
                        .weight(normalizedWeight)
                        .build();
                tags.add(tag);
            }
        }

        ResultResponse tagsCollection = new ResultResponse(tags);
        return ResponseEntity.ok(tagsCollection);
    }

    @PostMapping(value = "/moderation")
    public ResponseEntity<AbstractResponse> moderation(@RequestBody ModerationForm moderationForm) {
        long postId = moderationForm.getPostId();
        String status = moderationForm.getDecision();
        long userId = authService.getAuthorizedUserId();
        boolean result = true;
        switch (status) {
            case "accept":
                postService.setModerationStatus(userId, postId, ACCEPTED);
                break;
            case "decline":
                postService.setModerationStatus(userId, postId, DECLINED);
                break;
            default:
                result = false;
        }
        return ResponseEntity.ok(new ResultResponse(result));
    }

    @PostMapping(value = "/comment")
    public ResponseEntity<AbstractResponse> addComment(@RequestBody NewCommentForm newCommentForm) {
        long postId = newCommentForm.getPostId();
        Object parentIdObj = newCommentForm.getParentIdObj();
        String textWithHtml = newCommentForm.getText();
        String textWithoutHtml = Jsoup.parse(textWithHtml).text();

        User user = userService.findById(authService.getAuthorizedUserId());
        Post post = postService.findById(postId);

        if (post == null) {
            ResultResponse message = new ResultResponse(MESSAGE_POST_NOT_FOUND);
            return ResponseEntity.badRequest().body(message);
        }

        int minLengthText = 5;
        if (textWithoutHtml.length() < minLengthText) {
            ErrorResponse errors = new ErrorResponse();
            errors.setText(MESSAGE_COMMENT_SHORT);
            ResultResponse errorResponse = new ResultResponse(errors);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        PostComment comment = PostComment.create(user, post, textWithHtml);
        if (parentIdObj instanceof Integer) {
            long parentId = (int) parentIdObj;
            PostComment parent = postCommentService.findById(parentId);
            if (parent == null) {
                ResultResponse message = new ResultResponse(MESSAGE_COMMENT_NOT_FOUND);
                return ResponseEntity.badRequest().body(message);
            }
            comment.setParent(parent);
        }
        long commentId = postCommentService.add(comment).getId();
        ResultResponse successfulResponse = new ResultResponse(commentId);
        return ResponseEntity.ok(successfulResponse);
    }

    // С изображением
    @PostMapping(value = "/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AbstractResponse> updateProfile(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "removePhoto") Object removePhotoObj,
            @RequestParam(value = "photo") MultipartFile file,
            @RequestPart(value = "name", required = false) String name,
            @RequestPart(value = "password", required = false) String password
    ) {
        String photo = ImageUtil.resizeImageAndUpload(file);
        Integer removePhoto = removePhotoObj instanceof String ?
                Integer.parseInt((String) removePhotoObj) :
                (Integer) removePhotoObj;
        UpdateProfileForm updateProfileForm = new UpdateProfileForm(name, email, password, removePhoto, photo);
        return new ResponseEntity<>(updateProfile(updateProfileForm).getBody(), HttpStatus.OK);
    }

    // Без изображения
    @PostMapping(value = "/profile/my", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AbstractResponse> updateProfile(
            @RequestBody UpdateProfileForm updateProfileForm
    ) {
        String name = updateProfileForm.getName();
        String email = updateProfileForm.getEmail();
        String password = updateProfileForm.getPassword();
        Integer removePhoto = updateProfileForm.getRemovePhoto();
        String photo = updateProfileForm.getPhoto();

        User user = userService.findById(authService.getAuthorizedUserId());
        boolean result = true;
        ErrorResponse errors = new ErrorResponse();

        if (email == null) {
            errors.setEmail(MESSAGE_EMAIL_EMPTY);
            result = false;
        } else if (!email.equals(user.getEmail()) && userService.emailExists(email)) {
            errors.setEmail(MESSAGE_EMAIL_EXISTS);
            result = false;
        }

        if (userService.nameIsInvalid(name, errors)) {
            result = false;
        }

        if (password != null && userService.passwordIsInvalid(password, errors)) {
            result = false;
        }

        ResultResponse response = new ResultResponse(result);
        if (result) {
            if (!email.equals(user.getEmail())) {
                user.setEmail(email);
            }
            if (!name.equals(user.getName())) {
                user.setName(name);
            }
            if (password != null && !password.equals(user.getPassword())) {
                user.setPassword(password);
            }
            if (removePhoto != null) {
                user.setPhoto(photo);
            }

            userService.update(user);
        } else {
            response.setErrors(errors);
        }

        return ResponseEntity.ok(response);
    }
}
