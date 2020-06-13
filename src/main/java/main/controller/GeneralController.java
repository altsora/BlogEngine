package main.controller;

import main.model.entities.*;
import main.model.enums.ActivesType;
import main.model.enums.ModerationStatusType;
import main.model.enums.SettingsCodeType;
import main.model.enums.SettingsValueType;
import main.responses.BlogDTO;
import main.responses.CalendarResponseDTO;
import main.responses.CollectionTagsResponseDTO;
import main.responses.TagDTO;
import main.services.*;
import main.servlet.AuthorizeServlet;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class GeneralController {
    private AuthorizeServlet authorizeServlet;
    private GlobalSettingsService globalSettingsService;
    private PostCommentService postCommentService;
    private PostService postService;
    private PostVoteService postVoteService;
    private TagService tagService;
    private UserService userService;

    @Autowired
    public GeneralController(AuthorizeServlet authorizeServlet, GlobalSettingsService globalSettingsService,
                             PostCommentService postCommentService, PostService postService,
                             PostVoteService postVoteService, TagService tagService,
                             UserService userService) {
        this.authorizeServlet = authorizeServlet;
        this.globalSettingsService = globalSettingsService;
        this.postCommentService = postCommentService;
        this.postService = postService;
        this.postVoteService = postVoteService;
        this.tagService = tagService;
        this.userService = userService;
    }

    //==================================================================================================================

    @GetMapping(value = "/api/init")
    @ResponseBody
    public BlogDTO init() {
        BlogDTO blogDTO = new BlogDTO();
        blogDTO.setTitle("DevPub");
        blogDTO.setSubtitle("Рассказы разработчиков");
        blogDTO.setPhone("+7 903 666-44-55");
        blogDTO.setEmail("mail@mail.ru");
        blogDTO.setCopyright("Александр Вергун");
        blogDTO.setCopyrightFrom("2005");
        return blogDTO;
    }

    @GetMapping(value = "/api/settings")
    @ResponseBody
    public ResponseEntity getSettings() {
        JSONObject response = new JSONObject();
        List<GlobalSetting> globalSettings = globalSettingsService.findAll();
        for (GlobalSetting setting : globalSettings) {
            boolean enable = setting.getValue() == SettingsValueType.YES;
            String code = setting.getCode().name();
            response.put(code, enable);
        }
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PutMapping(value = "/api/settings")
    @ResponseBody
    public ResponseEntity saveSettings(@RequestBody JSONObject request) {
        Boolean multiUserModeValue = (Boolean) request.get("MULTIUSER_MODE");
        Boolean postPreModerationValue = (Boolean) request.get("POST_PREMODERATION");
        Boolean statisticsIsPublicValue = (Boolean) request.get("STATISTICS_IS_PUBLIC");
        if (authorizeServlet.isUserAuthorize()) {
            User user = userService.findById(authorizeServlet.getAuthorizedUserId());
            if (user.getIsModerator() == (byte) 1) {
                if (multiUserModeValue != null) {
                    globalSettingsService.setValue(SettingsCodeType.MULTIUSER_MODE, multiUserModeValue);
                }
                if (postPreModerationValue != null) {
                    globalSettingsService.setValue(SettingsCodeType.POST_PREMODERATION, postPreModerationValue);
                }
                if (statisticsIsPublicValue != null) {
                    globalSettingsService.setValue(SettingsCodeType.STATISTICS_IS_PUBLIC, statisticsIsPublicValue);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/api/calendar")
    public ResponseEntity<CalendarResponseDTO> getCalendar(@RequestParam(value = "year", required = false) Integer year) {
        if (year == null) {
            year = LocalDateTime.now(ZoneId.of("UTC")).getYear();
        }
        Map<String, Long> datesAndCountPosts = postService.getDateAndCountPosts(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, year);
        JSONObject posts = new JSONObject(datesAndCountPosts);
        List<Integer> years = postService.findAllYearsOfPublication(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED);
        CalendarResponseDTO calendarResponseDTO = new CalendarResponseDTO(years, posts);
        return new ResponseEntity<>(calendarResponseDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/api/statistics/all")
    public ResponseEntity getBlogStatistics() {
        if (globalSettingsService.settingStatisticsIsPublicIsEnabled()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            int postsCount = postService.getTotalCountOfPosts(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED);
            int likesCount = postVoteService.getTotalCountLikes();
            int dislikesCount = postVoteService.getTotalCountDislikes();
            int viewsCount = postService.getTotalCountView(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED);
            LocalDateTime localDateTime = postService.getDateOfTheEarliestPost(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED);
            String firstPublication = localDateTime == null ?
                    "-" : formatter.format(localDateTime);

            JSONObject response = new JSONObject();
            response.put("postsCount", postsCount);
            response.put("likesCount", likesCount);
            response.put("dislikesCount", dislikesCount);
            response.put("viewsCount", viewsCount);
            response.put("firstPublication", firstPublication);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            if (authorizeServlet.isUserAuthorize()) {
                return new ResponseEntity<>(getMyStatistics().getBody(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
    }

    @GetMapping(value = "/api/statistics/my")
    public ResponseEntity getMyStatistics() {
        long userId = authorizeServlet.getAuthorizedUserId();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        int postsCount = postService.getTotalCountOfPostsByUserId(userId);
        int likesCount = postVoteService.getTotalCountLikesByUserId(userId);
        int dislikesCount = postVoteService.getTotalCountDislikesByUserId(userId);
        int viewsCount = postService.getTotalCountViewByUserId(userId);
        LocalDateTime localDateTime = postService.getDateOfTheEarliestPostByUserId(userId);
        String firstPublication = localDateTime == null ?
                "-" : formatter.format(localDateTime);

        JSONObject response = new JSONObject();
        response.put("postsCount", postsCount);
        response.put("likesCount", likesCount);
        response.put("dislikesCount", dislikesCount);
        response.put("viewsCount", viewsCount);
        response.put("firstPublication", firstPublication);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/tag")
    @ResponseBody
    public ResponseEntity<CollectionTagsResponseDTO> getTagList(@RequestParam(value = "query", required = false) String query) {
        List<Tag> tagListRep = (query == null || query.equals("")) ?
                tagService.findAll() :
                tagService.findAllTagsByQuery(query);
        List<Double> weights = new ArrayList<>();
        int totalNumberOfPosts = postService.getTotalCountOfPosts(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED);
        double maxWeight = -1;
        for (Tag tagRep : tagListRep) {
            int countPosts = postService.getTotalCountOfPostsByTag(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, tagRep.getName());
            double weight = (double) countPosts / totalNumberOfPosts;
            weights.add(weight);
            if (weight > maxWeight) {
                maxWeight = weight;
            }
        }

        List<TagDTO> tags = new ArrayList<>();
        for (int i = 0; i < tagListRep.size(); i++) {
            String tagName = tagListRep.get(i).getName();
            double normalizedWeight = weights.get(i) / maxWeight;
            TagDTO tagDTO = new TagDTO(tagName, normalizedWeight);
            tags.add(tagDTO);
        }

        CollectionTagsResponseDTO collectionTagsResponseDTO = new CollectionTagsResponseDTO();
        collectionTagsResponseDTO.setTags(tags);
        return new ResponseEntity<>(collectionTagsResponseDTO, HttpStatus.OK);
    }

    @PostMapping(value = "/api/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public String uploadImage(@RequestPart(value = "image") MultipartFile file) {
        String name = file.getOriginalFilename();
        String formatName = name.split("\\.")[1];
        StringBuilder mainPath = new StringBuilder("src/main/resources/upload/");
        String fileName = getRandomFileName(mainPath, formatName);
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(mainPath.toString())));
                stream.write(bytes);
                stream.close();
                return fileName;
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
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
        return new ResponseEntity(buffer, headers, HttpStatus.OK);
    }

    @PostMapping(value = "/api/moderation")
    @ResponseBody
    public void moderation(@RequestBody JSONObject request) {
        long postId = (int) request.get("post_id");
        String status = (String) request.get("decision");
        long userId = authorizeServlet.getAuthorizedUserId();
        switch (status) {
            case "accept":
                postService.setModerationStatus(userId, postId, ModerationStatusType.ACCEPTED);
                break;
            case "decline":
                postService.setModerationStatus(userId, postId, ModerationStatusType.DECLINED);
                break;
        }
    }

    @PostMapping(value = "/api/comment")
    public ResponseEntity addComment(@RequestBody JSONObject request) {
        JSONObject notFoundResponse = new JSONObject();
        JSONObject errorResponse = new JSONObject();
        JSONObject successfulResponse = new JSONObject();

        long postId = (int) request.get("post_id");
        Object parentIdObj = request.get("parent_id");
        String text = (String) request.get("text");

        User user = userService.findById(authorizeServlet.getAuthorizedUserId());
        Post post = postService.findById(postId);
        if (post == null) {
            notFoundResponse.put("result", false);
            notFoundResponse.put("message", "Пост с ID = " + postId + " не существует.");
            return new ResponseEntity<>(notFoundResponse, HttpStatus.BAD_REQUEST);
        }

        int minLengthText = 10;
        if (text.length() < minLengthText) {
            errorResponse.put("result", false);
            JSONObject errorText = new JSONObject();
            errorText.put("text", "Текст комментария не задан или слишком короткий");
            errorResponse.put("errors", errorText);
            return new ResponseEntity(errorResponse, HttpStatus.OK);
        }

        PostComment comment = new PostComment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setText(text);
        comment.setTime(LocalDateTime.now(ZoneId.of("UTC")));

        if (parentIdObj instanceof Integer) {
            long parentId = (int) parentIdObj;
            PostComment parent = postCommentService.findById(parentId);
            if (parent == null) {
                notFoundResponse.put("result", false);
                notFoundResponse.put("message", "Комментарий с ID = " + parentId + " не существует.");
                return new ResponseEntity<>(notFoundResponse, HttpStatus.BAD_REQUEST);
            }
            comment.setParent(parent);
        }
        long commentId = postCommentService.add(comment).getId();
        successfulResponse.put("id", commentId);
        return new ResponseEntity<>(successfulResponse, HttpStatus.OK);
    }

    // С изображением
    @PostMapping(value = "/api/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity updateProfile(
            @RequestParam(value = "email") String email,
            @RequestParam(value = "removePhoto") Object removePhoto,
            @RequestParam(value = "photo") MultipartFile file,
            @RequestPart(value = "name") String name,
            @RequestPart(value = "password", required = false) String password
    ) {
        String photo = resizeImageAndUpload(file);
        JSONObject response = new JSONObject();
        response.put("email", email);
        response.put("name", name);
        response.put("password", password);
        response.put("photo", photo);

        if (removePhoto instanceof String) {
            response.put("removePhoto", Integer.parseInt((String) removePhoto));
        } else {
            response.put("removePhoto", removePhoto);
        }

        return new ResponseEntity(updateProfile(response).getBody(), HttpStatus.OK);
    }

    // Без изображения
    @PostMapping(value = "/api/profile/my", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateProfile(
            @RequestBody JSONObject request
    ) {
//        System.err.println("\nContent-Type: application/json");
        String name = (String) request.get("name");
        String email = (String) request.get("email");
        String password = (String) request.get("password");
        Integer removePhoto = (Integer) request.get("removePhoto");
        String photo = (String) request.get("photo");

        boolean result = true;
        JSONObject response = new JSONObject();
        JSONObject errors = new JSONObject();
        User updatedUser = userService.findById(authorizeServlet.getAuthorizedUserId());

        if (!email.equals(updatedUser.getEmail()) && userService.emailExists(email)) {
            errors.put("email", "Этот e-mail уже зарегистрирован");
            result = false;
        }

        if (userService.nameIsInvalid(name, errors)) {
            result = false;
        }

        if (password != null) {
            if (userService.passwordIsInvalid(password, errors)) {
                result = false;
            }
        }

        response.put("result", result);
        if (result) {
            if (!email.equals(updatedUser.getEmail())) {
                updatedUser.setEmail(email);
            }
            if (!name.equals(updatedUser.getName())) {
                updatedUser.setName(name);
            }
            if (password != null) {
                if (!password.equals(updatedUser.getPassword())) {
                    updatedUser.setPassword(password);
                }
            }
            if (removePhoto != null) {
                updatedUser.setPhoto(photo);
            }

            userService.update(updatedUser);
        } else {
            response.put("errors", errors);
        }

        return new ResponseEntity(response, HttpStatus.OK);
    }


    //==================================================================================================================

    private String getRandomFileName(StringBuilder mainPath, String format) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz";
        String numbers = "0123456789";
        StringBuilder fileName = new StringBuilder("/upload/");
        int lengthPath = 3;
        int fileNameLength = 5;
        for (int i = 0; i < 3; i++) {
            StringBuilder folderName = new StringBuilder();
            for (int j = 0; j < lengthPath; j++) {
                int index = (int) (Math.random() * alphabet.length());
                folderName.append(alphabet.charAt(index));
            }
            File folder = new File(mainPath.toString() + folderName);
            if (!folder.exists()) {
                folder.mkdir();
            }
            mainPath.append(folderName.toString()).append("/");
            fileName.append(folderName.toString()).append("/");
        }
        for (int i = 0; i < fileNameLength; i++) {
            int index = (int) (Math.random() * numbers.length());
            mainPath.append(numbers.charAt(index));
            fileName.append(numbers.charAt(index));
        }

        mainPath.append(".").append(format);
        fileName.append(".").append(format);
        File image = new File(mainPath.toString());
        if (!image.exists()) {
            try {
                image.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileName.toString();
    }

    private String resizeImageAndUpload(MultipartFile file) {
        int width = 35;
        int height = 35;
        String formatName = file.getOriginalFilename().split("\\.")[1];
        // MultipartFile -> Image
        BufferedImage image = null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(file.getBytes())){
            image = ImageIO.read(bais);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Resize
        if (image.getWidth() > width && image.getHeight() > height) {
            int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
            BufferedImage resizeImage = new BufferedImage(width, height, type);
            Graphics2D g = resizeImage.createGraphics();
            g.drawImage(image, 0, 0, width, height, null);
            g.dispose();
            image = resizeImage;
        }
        // Image -> Bytes
        byte[] imageBytes = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, formatName, baos);
            imageBytes = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Bytes -> Upload
        StringBuilder mainPath = new StringBuilder("src/main/resources/upload/");
        String fileName = getRandomFileName(mainPath, formatName);
        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(mainPath.toString())))){
            stream.write(imageBytes);
            return fileName;
        } catch (IOException e) {
            return "";
        }
    }
}
