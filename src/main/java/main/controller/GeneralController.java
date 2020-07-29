package main.controller;

import lombok.RequiredArgsConstructor;
import main.model.entity.*;
import main.model.enums.ActivityStatus;
import main.model.enums.ModerationStatus;
import main.model.enums.SettingsCode;
import main.model.enums.SettingsValue;
import main.request.ModerationForm;
import main.request.SettingsForm;
import main.response.BlogDTO;
import main.service.*;
import main.servlet.AuthorizeServlet;
import main.util.TimeUtil;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class GeneralController {
    private final AuthorizeServlet authorizeServlet;
    private final GlobalSettingsService globalSettingsService;
    private final PostCommentService postCommentService;
    private final PostService postService;
    private final PostVoteService postVoteService;
    private final TagService tagService;
    private final UserService userService;
    private final BlogDTO blog;

    //==================================================================================================================

    @GetMapping(value = "/api/init")
    @ResponseBody
    public BlogDTO init() {
        return blog;
    }

    @GetMapping(value = "/api/settings")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> getSettings() {
        JSONObject response = new JSONObject();
        List<GlobalSetting> globalSettings = globalSettingsService.findAll();
        for (GlobalSetting setting : globalSettings) {
            boolean enable = setting.getValue() == SettingsValue.YES;
            String code = setting.getCode().name();
            response.put(code, enable);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/api/settings")
    public ResponseEntity<String> saveSettings(@RequestBody SettingsForm settingsForm) {
        boolean multiUserModeValue = settingsForm.isMultiUserModeValue();
        boolean postPreModerationValue = settingsForm.isPostPreModerationValue();
        boolean statisticsIsPublicValue = settingsForm.isStatisticsIsPublicValue();
        if (authorizeServlet.isUserAuthorize()) {
            User user = userService.findById(authorizeServlet.getAuthorizedUserId());
            if (user.isModerator()) {
                globalSettingsService.setValue(SettingsCode.MULTIUSER_MODE, multiUserModeValue);
                globalSettingsService.setValue(SettingsCode.POST_PREMODERATION, postPreModerationValue);
                globalSettingsService.setValue(SettingsCode.STATISTICS_IS_PUBLIC, statisticsIsPublicValue);
            }
            return ResponseEntity.ok().body("Settings saved successfully");
        }
        return ResponseEntity.ok().body("Changes were not saved");
    }

    @GetMapping(value = "/api/calendar")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> getCalendar(@RequestParam(value = "year", required = false) Integer year) {
        if (year == null) {
            year = LocalDateTime.now(TimeUtil.TIME_ZONE).getYear();
        }
        Map<String, Long> datesAndCountPosts = postService.getDateAndCountPosts(ActivityStatus.ACTIVE, ModerationStatus.ACCEPTED, year);
        JSONObject posts = new JSONObject(datesAndCountPosts);
        List<Integer> years = postService.findAllYearsOfPublication(ActivityStatus.ACTIVE, ModerationStatus.ACCEPTED);
        JSONObject calendar = new JSONObject();
        calendar.put("years", years);
        calendar.put("posts", posts);
        return ResponseEntity.ok(calendar);
    }

    @GetMapping(value = "/api/statistics/all")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> getBlogStatistics() {
        if (globalSettingsService.settingStatisticsIsPublicIsEnabled()) {
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            int postsCount = postService.getTotalCountOfPosts(ActivityStatus.ACTIVE, ModerationStatus.ACCEPTED);
            int likesCount = postVoteService.getTotalCountLikes();
            int dislikesCount = postVoteService.getTotalCountDislikes();
            int viewsCount = postService.getTotalCountView(ActivityStatus.ACTIVE, ModerationStatus.ACCEPTED);
            LocalDateTime localDateTime = postService.getDateOfTheEarliestPost(ActivityStatus.ACTIVE, ModerationStatus.ACCEPTED);
            long firstPublication = localDateTime == null ?
                    0 : TimeUtil.getTimestampFromLocalDateTime(localDateTime);

            JSONObject response = new JSONObject();
            response.put("postsCount", postsCount);
            response.put("likesCount", likesCount);
            response.put("dislikesCount", dislikesCount);
            response.put("viewsCount", viewsCount);
            response.put("firstPublication", firstPublication);

            return ResponseEntity.ok(response);
        } else {
            if (authorizeServlet.isUserAuthorize()) {
                return new ResponseEntity<>(getMyStatistics().getBody(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
    }

    @GetMapping(value = "/api/statistics/my")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> getMyStatistics() {
        long userId = authorizeServlet.getAuthorizedUserId();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        int postsCount = postService.getTotalCountOfPostsByUserId(userId);
        int likesCount = postVoteService.getTotalCountLikesByUserId(userId);
        int dislikesCount = postVoteService.getTotalCountDislikesByUserId(userId);
        int viewsCount = postService.getTotalCountViewByUserId(userId);
        LocalDateTime localDateTime = postService.getDateOfTheEarliestPostByUserId(userId);
        long firstPublication = localDateTime == null ?
                0 : TimeUtil.getTimestampFromLocalDateTime(localDateTime);

        JSONObject response = new JSONObject();
        response.put("postsCount", postsCount);
        response.put("likesCount", likesCount);
        response.put("dislikesCount", dislikesCount);
        response.put("viewsCount", viewsCount);
        response.put("firstPublication", firstPublication);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/tag")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> getTagList(@RequestParam(value = "query", required = false) String query) {
//        double minNormalizedWeight = 0.3d;
//        List<Tag> tagListRep = (query == null || query.equals("")) ?
//                tagService.findAll() :
//                tagService.findAllTagsByQuery(query);
//        List<Double> weights = new ArrayList<>();
//        int totalNumberOfPosts = postService.getTotalCountOfPosts(ActivityStatus.ACTIVE, ModerationStatus.ACCEPTED);
//        double maxWeight = -1, weight;
//        int countPosts;
//        for (Tag tagRep : tagListRep) {
//            countPosts = postService.getTotalCountOfPostsByTag(ActivityStatus.ACTIVE, ModerationStatus.ACCEPTED, tagRep.getName());
//            weight = (double) countPosts / totalNumberOfPosts;
//            weights.add(weight);
//            if (weight > maxWeight) {
//                maxWeight = weight;
//            }
//        }
//
//        List<TagDTO> tags = new ArrayList<>();
//        int size = tagListRep.size();
//        for (int i = 0; i < size; i++) {
//            String tagName = tagListRep.get(i).getName();
//            double normalizedWeight = weights.get(i) / maxWeight;
//            if (Double.compare(normalizedWeight, minNormalizedWeight) >= 0) {
//                TagDTO tag = TagDTO.builder()
//                        .name(tagName)
//                        .weight(normalizedWeight)
//                        .build();
//                tags.add(tag);
//            }
//        }
//
//        JSONObject tagsCollection = new JSONObject();
//        tagsCollection.put("tags", tags);
//        return ResponseEntity.ok(tagsCollection);
        return ResponseEntity.ok(null);
    }

//    @PostMapping(value = "/api/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @ResponseBody
//    public String uploadImage(@RequestPart(value = "image") MultipartFile file) {
//        String name = file.getOriginalFilename();
//        String formatName = Objects.requireNonNull(name).split("\\.")[1];
//        StringBuilder mainPath = new StringBuilder("src/main/resources/upload/");
//        String fileName = ImageUtil.getRandomImageName(mainPath, formatName);
////        String fileName = getRandomFileName(mainPath, formatName);
//        if (!file.isEmpty()) {
//            try {
//                byte[] bytes = file.getBytes();
//                BufferedOutputStream stream =
//                        new BufferedOutputStream(new FileOutputStream(new File(mainPath.toString())));
//                stream.write(bytes);
//                stream.close();
//                return fileName;
//            } catch (Exception e) {
//                return null;
//            }
//        } else {
//            return null;
//        }
//    }
//
//    @GetMapping(value = "/upload/{dir1}/{dir2}/{dir3}/{fileName}")
//    public ResponseEntity getImage(
//            @PathVariable(value = "dir1") String dir1,
//            @PathVariable(value = "dir2") String dir2,
//            @PathVariable(value = "dir3") String dir3,
//            @PathVariable(value = "fileName") String fileName
//    ) {
//        String fullPath = String.format("src/main/resources/upload/%s/%s/%s/%s", dir1, dir2, dir3, fileName);
//
//        byte[] buffer = null;
//        try {
//            buffer = Files.readAllBytes(Paths.get(fullPath));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String format = fullPath.split("\\.")[1];
//        final HttpHeaders headers = new HttpHeaders();
//        if (format.equalsIgnoreCase("png"))
//            headers.setContentType(MediaType.IMAGE_PNG);
//        if (format.equalsIgnoreCase("jpg"))
//            headers.setContentType(MediaType.IMAGE_JPEG);
//        if (format.equalsIgnoreCase("gif"))
//            headers.setContentType(MediaType.IMAGE_GIF);
//        return new ResponseEntity<>(buffer, headers, HttpStatus.OK);
//    }

    @PostMapping(value = "/api/moderation")
    public void moderation(@RequestBody ModerationForm moderationForm) {
        long postId = moderationForm.getPostId();
        String status = moderationForm.getDecision();
        long userId = authorizeServlet.getAuthorizedUserId();
        switch (status) {
            case "accept":
                postService.setModerationStatus(userId, postId, ModerationStatus.ACCEPTED);
                break;
            case "decline":
                postService.setModerationStatus(userId, postId, ModerationStatus.DECLINED);
                break;
        }
    }

//    @PostMapping(value = "/api/comment")
//    @SuppressWarnings("unchecked")
//    public ResponseEntity<JSONObject> addComment(@RequestBody NewCommentForm newCommentForm) {
//        JSONObject notFoundResponse = new JSONObject();
//        JSONObject errorResponse = new JSONObject();
//        JSONObject successfulResponse = new JSONObject();
//
//        long postId = newCommentForm.getPostId();
//        Object parentIdObj = newCommentForm.getParentIdObj();
//        String text = newCommentForm.getText();
//
//        User user = userService.findById(authorizeServlet.getAuthorizedUserId());
//        Post post = postService.findById(postId);
//        if (post == null) {
//            notFoundResponse.put("result", false);
//            notFoundResponse.put("message", "Пост с ID = " + postId + " не существует.");
//            return new ResponseEntity<>(notFoundResponse, HttpStatus.BAD_REQUEST);
//        }
//
//        int minLengthText = 10;
//        if (text.length() < minLengthText) {
//            errorResponse.put("result", false);
//            JSONObject errorText = new JSONObject();
//            errorText.put("text", "Текст комментария не задан или слишком короткий");
//            errorResponse.put("errors", errorText);
//            return new ResponseEntity<>(errorResponse, HttpStatus.OK);
//        }
//
//        PostComment comment = PostComment.create(user, post, text);
//        if (parentIdObj instanceof Integer) {
//            long parentId = (int) parentIdObj;
//            PostComment parent = postCommentService.findById(parentId);
//            if (parent == null) {
//                notFoundResponse.put("result", false);
//                notFoundResponse.put("message", "Комментарий с ID = " + parentId + " не существует.");
//                return new ResponseEntity<>(notFoundResponse, HttpStatus.BAD_REQUEST);
//            }
//            comment.setParent(parent);
//        }
//        long commentId = postCommentService.add(comment).getId();
//        successfulResponse.put("id", commentId);
//        return ResponseEntity.ok(successfulResponse);
//    }
//
//    // С изображением
//    @PostMapping(value = "/api/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @SuppressWarnings("unchecked")
//    public ResponseEntity<JSONObject> updateProfile(
//            @RequestParam(value = "email") String email,
//            @RequestParam(value = "removePhoto") Object removePhotoObj,
//            @RequestParam(value = "photo") MultipartFile file,
//            @RequestPart(value = "name") String name,
//            @RequestPart(value = "password", required = false) String password
//    ) {
//        String photo = ImageUtil.resizeImageAndUpload(file);
//        Integer removePhoto = removePhotoObj instanceof String ?
//                Integer.parseInt((String) removePhotoObj) :
//                (Integer) removePhotoObj;
//        UpdateProfileForm updateProfileForm = new UpdateProfileForm(name, email, password, removePhoto, photo);
//        return new ResponseEntity<>(updateProfile(updateProfileForm).getBody(), HttpStatus.OK);
//    }
//
//    // Без изображения
//    @PostMapping(value = "/api/profile/my", consumes = MediaType.APPLICATION_JSON_VALUE)
//    @SuppressWarnings("unchecked")
//    public ResponseEntity<JSONObject> updateProfile(
//            @RequestBody UpdateProfileForm updateProfileForm
//
//    ) {
//        String name = updateProfileForm.getName();
//        String email = updateProfileForm.getEmail();
//        String password = updateProfileForm.getPassword();
//        Integer removePhoto = updateProfileForm.getRemovePhoto();
//        String photo = updateProfileForm.getPhoto();
//
//        boolean result = true;
//        JSONObject response = new JSONObject();
//        JSONObject errors = new JSONObject();
//        User updatedUser = userService.findById(authorizeServlet.getAuthorizedUserId());
//
//        if (email == null) {
//            errors.put("email", "Укажите e-mail");
//            result = false;
//        } else {
//            if (!email.equals(updatedUser.getEmail()) && userService.emailExists(email)) {
//                errors.put("email", "Этот e-mail уже зарегистрирован");
//                result = false;
//            }
//        }
//
//        if (userService.nameIsInvalid(name, errors)) {
//            result = false;
//        }
//
//        if (password != null) {
//            if (userService.passwordIsInvalid(password, errors)) {
//                result = false;
//            }
//        }
//
//        response.put("result", result);
//        if (result) {
//            if (!email.equals(updatedUser.getEmail())) {
//                updatedUser.setEmail(email);
//            }
//            if (!name.equals(updatedUser.getName())) {
//                updatedUser.setName(name);
//            }
//            if (password != null) {
//                if (!password.equals(updatedUser.getPassword())) {
//                    updatedUser.setPassword(password);
//                }
//            }
//            if (removePhoto != null) {
//                updatedUser.setPhoto(photo);
//            }
//
//            userService.update(updatedUser);
//        } else {
//            response.put("errors", errors);
//        }
//
//        return ResponseEntity.ok(response);
//    }
}
