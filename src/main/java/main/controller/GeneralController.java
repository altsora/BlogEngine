package main.controller;

import lombok.RequiredArgsConstructor;
import main.api.requests.ModerationForm;
import main.api.requests.NewCommentForm;
import main.api.requests.SettingsForm;
import main.api.requests.UpdateProfileForm;
import main.api.responses.*;
import main.model.entity.*;
import main.services.*;
import main.services.impl.AuthServiceImpl;
import main.utils.ImageUtil;
import main.utils.TimeUtil;
import org.jsoup.Jsoup;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import static main.model.enums.ActivityStatus.ACTIVE;
import static main.model.enums.ModerationStatus.ACCEPTED;
import static main.model.enums.ModerationStatus.DECLINED;
import static main.model.enums.SettingsCode.*;
import static main.model.enums.SettingsValue.YES;
import static main.utils.MessageUtil.*;

@RestController
@RequiredArgsConstructor
public class GeneralController {
    private final AuthServiceImpl authServiceImpl;
    private final BlogDTO blog;
    private final GlobalSettingsService globalSettingsService;
    private final PostCommentService postCommentService;
    private final PostService postService;
    private final PostVoteService postVoteService;
    private final TagService tagService;
    private final UserService userService;

    //==================================================================================================================

    @GetMapping(value = "/api/init")
    public BlogDTO init() {
        return blog;
    }

    @GetMapping(value = "/api/settings")
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

    @PutMapping(value = "/api/settings")
    public ResponseEntity<String> saveSettings(@RequestBody SettingsForm settingsForm) {
        if (authServiceImpl.isUserAuthorize()) {
            User user = userService.findById(authServiceImpl.getAuthorizedUserId());
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

    @GetMapping(value = "/api/calendar")
    public ResponseEntity<CalendarDTO> getCalendar(@RequestParam(value = "year", required = false) Integer year) {
        if (year == null) {
            year = LocalDateTime.now(TimeUtil.TIME_ZONE).getYear();
        }
        List<Integer> years = postService.findAllYearsOfPublication(ACTIVE, ACCEPTED);
        Map<String, Long> posts = postService.getDateAndCountPosts(ACTIVE, ACCEPTED, year);
        CalendarDTO calendar = CalendarDTO.builder().years(years).posts(posts).build();
        return ResponseEntity.ok(calendar);
    }

    @GetMapping(value = "/api/statistics/all")
    public ResponseEntity<StatisticDTO> getBlogStatistics() {
        if (globalSettingsService.settingStatisticsIsPublicIsEnabled()) {
            int dislikesCount = postVoteService.getTotalCountDislikes();
            int likesCount = postVoteService.getTotalCountLikes();
            int postsCount = postService.getTotalCountOfPosts(ACTIVE, ACCEPTED);
            int viewsCount = postService.getTotalCountView(ACTIVE, ACCEPTED);
            LocalDateTime localDateTime = postService.getDateOfTheEarliestPost(ACTIVE, ACCEPTED);
            long firstPublication = localDateTime == null ?
                    0L : TimeUtil.getTimestampFromLocalDateTime(localDateTime);

            StatisticDTO statistic = StatisticDTO.builder()
                    .dislikesCount(dislikesCount)
                    .firstPublication(firstPublication)
                    .likesCount(likesCount)
                    .postsCount(postsCount)
                    .viewsCount(viewsCount)
                    .build();

            return ResponseEntity.ok(statistic);
        }
        if (authServiceImpl.isUserAuthorize()) {
            return new ResponseEntity<>(getMyStatistics().getBody(), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }

    @GetMapping(value = "/api/statistics/my")
    public ResponseEntity<StatisticDTO> getMyStatistics() {
        long userId = authServiceImpl.getAuthorizedUserId();
        int likesCount = postVoteService.getTotalCountLikesByUserId(userId);
        int dislikesCount = postVoteService.getTotalCountDislikesByUserId(userId);
        int postsCount = postService.getTotalCountOfPostsByUserId(userId);
        int viewsCount = postService.getTotalCountViewByUserId(userId);
        LocalDateTime localDateTime = postService.getDateOfTheEarliestPostByUserId(userId);
        long firstPublication = localDateTime == null ?
                0L : TimeUtil.getTimestampFromLocalDateTime(localDateTime);

        StatisticDTO statistic = StatisticDTO.builder()
                .dislikesCount(dislikesCount)
                .firstPublication(firstPublication)
                .likesCount(likesCount)
                .postsCount(postsCount)
                .viewsCount(viewsCount)
                .build();

        return ResponseEntity.ok(statistic);
    }

    @GetMapping(value = "/api/tag")
    public ResponseEntity<ResultDTO> getTagList(@RequestParam(value = "query", required = false) String query) {
        double minNormalizedWeight = 0.3d;
        List<Tag> tagListRep = (query == null || query.equals("")) ?
                tagService.findAll() :
                tagService.findAllTagsByQuery(query);
        List<Double> weights = new ArrayList<>();
        int totalNumberOfPosts = postService.getTotalCountOfPosts(ACTIVE, ACCEPTED);
        double maxWeight = -1, weight;
        int countPosts;
        for (Tag tagRep : tagListRep) {
            countPosts = postService.getTotalCountOfPostsByTag(ACTIVE, ACCEPTED, tagRep.getName());
            weight = (double) countPosts / totalNumberOfPosts;
            weights.add(weight);
            if (weight > maxWeight) {
                maxWeight = weight;
            }
        }

        List<TagDTO> tags = new ArrayList<>();
        int size = tagListRep.size();
        for (int i = 0; i < size; i++) {
            String tagName = tagListRep.get(i).getName();
            double normalizedWeight = weights.get(i) / maxWeight;
            if (Double.compare(normalizedWeight, minNormalizedWeight) >= 0) {
                TagDTO tag = TagDTO.builder()
                        .name(tagName)
                        .weight(normalizedWeight)
                        .build();
                tags.add(tag);
            }
        }

        ResultDTO tagsCollection = new ResultDTO(tags);
        return ResponseEntity.ok(tagsCollection);
    }

    @PostMapping(value = "/api/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity uploadImage(@RequestPart(value = "image") MultipartFile file) {
        String name = file.getOriginalFilename();
        String formatName = Objects.requireNonNull(name).split("\\.")[1];

        if (!formatName.equalsIgnoreCase("png") && !formatName.equalsIgnoreCase("jpg")) {
            ErrorsDTO errors = new ErrorsDTO();
            errors.setImage(MESSAGE_IMAGE_INVALID_FORMAT);
            ResultDTO response = new ResultDTO(errors);
            return ResponseEntity.badRequest().body(response);
        }

        if (!file.isEmpty()) {
            StringBuilder mainPath = new StringBuilder("src/main/resources/upload/");
            String fileName = ImageUtil.getRandomImageName(mainPath, formatName);
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(mainPath.toString())));
                stream.write(bytes);
                stream.close();
                return ResponseEntity.ok(fileName);
            } catch (Exception e) {
                ErrorsDTO errors = new ErrorsDTO();
                errors.setImage(MESSAGE_IMAGE_ERROR_LOAD);
                ResultDTO response = new ResultDTO(errors);
                return ResponseEntity.badRequest().body(response);
            }
        }
        return null;
    }

    @GetMapping(value = "/upload/{dir1}/{dir2}/{dir3}/{fileName}")
    public ResponseEntity getImage(
            @PathVariable(value = "dir1") String dir1,
            @PathVariable(value = "dir2") String dir2,
            @PathVariable(value = "dir3") String dir3,
            @PathVariable(value = "fileName") String fileName
    ) {
        String fullPath = String.format("src/main/resources/upload/%s/%s/%s/%s", dir1, dir2, dir3, fileName);

        byte[] buffer = null;
        try {
            buffer = Files.readAllBytes(Paths.get(fullPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String format = fullPath.split("\\.")[1];
        final HttpHeaders headers = new HttpHeaders();
        if (format.equalsIgnoreCase("png"))
            headers.setContentType(MediaType.IMAGE_PNG);
        if (format.equalsIgnoreCase("jpg"))
            headers.setContentType(MediaType.IMAGE_JPEG);
        if (format.equalsIgnoreCase("gif"))
            headers.setContentType(MediaType.IMAGE_GIF);
        return new ResponseEntity<>(buffer, headers, HttpStatus.OK);
    }

    @PostMapping(value = "/api/moderation")
    public ResponseEntity<ResultDTO> moderation(@RequestBody ModerationForm moderationForm) {
        long postId = moderationForm.getPostId();
        String status = moderationForm.getDecision();
        long userId = authServiceImpl.getAuthorizedUserId();
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
        return ResponseEntity.ok(new ResultDTO(result));
    }

    @PostMapping(value = "/api/comment")
    public ResponseEntity<ResultDTO> addComment(@RequestBody NewCommentForm newCommentForm) {
        long postId = newCommentForm.getPostId();
        Object parentIdObj = newCommentForm.getParentIdObj();
        String textWithHtml = newCommentForm.getText();
        String textWithoutHtml = Jsoup.parse(textWithHtml).text();

        User user = userService.findById(authServiceImpl.getAuthorizedUserId());
        Post post = postService.findById(postId);

        if (post == null) {
            ResultDTO message = new ResultDTO(MESSAGE_POST_NOT_FOUND);
            return ResponseEntity.badRequest().body(message);
        }

        int minLengthText = 5;
        if (textWithoutHtml.length() < minLengthText) {
            ErrorsDTO errors = new ErrorsDTO();
            errors.setText(MESSAGE_COMMENT_SHORT);
            ResultDTO errorResponse = new ResultDTO(errors);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        PostComment comment = PostComment.create(user, post, textWithHtml);
        if (parentIdObj instanceof Integer) {
            long parentId = (int) parentIdObj;
            PostComment parent = postCommentService.findById(parentId);
            if (parent == null) {
                ResultDTO message = new ResultDTO(MESSAGE_COMMENT_NOT_FOUND);
                return ResponseEntity.badRequest().body(message);
            }
            comment.setParent(parent);
        }
        long commentId = postCommentService.add(comment).getId();
        ResultDTO successfulResponse = new ResultDTO(commentId);
        return ResponseEntity.ok(successfulResponse);
    }

    // С изображением
    @PostMapping(value = "/api/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultDTO> updateProfile(
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
    @PostMapping(value = "/api/profile/my", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResultDTO> updateProfile(
            @RequestBody UpdateProfileForm updateProfileForm
    ) {
        String name = updateProfileForm.getName();
        String email = updateProfileForm.getEmail();
        String password = updateProfileForm.getPassword();
        Integer removePhoto = updateProfileForm.getRemovePhoto();
        String photo = updateProfileForm.getPhoto();

        User user = userService.findById(authServiceImpl.getAuthorizedUserId());
        boolean result = true;
        ErrorsDTO errors = new ErrorsDTO();

        if (email == null) {
            errors.setEmail(MESSAGE_EMAIL_EMPTY);
            result = false;
        } else {
            if (!email.equals(user.getEmail()) && userService.emailExists(email)) {
                errors.setEmail(MESSAGE_EMAIL_EXISTS);
                result = false;
            }
        }

        if (userService.nameIsInvalid(name, errors)) {
            result = false;
        }

        if (password != null) {
            if (userService.passwordIsInvalid(password, errors)) {
                result = false;
            }
        }

        ResultDTO response = new ResultDTO(result);
        if (result) {
            if (!email.equals(user.getEmail())) {
                user.setEmail(email);
            }
            if (!name.equals(user.getName())) {
                user.setName(name);
            }
            if (password != null) {
                if (!password.equals(user.getPassword())) {
                    user.setPassword(password);
                }
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
