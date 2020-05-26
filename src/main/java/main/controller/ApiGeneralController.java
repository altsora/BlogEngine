package main.controller;

import main.model.entities.GlobalSetting;
import main.model.entities.Tag;
import main.model.entities.enums.ActivesType;
import main.model.entities.enums.ModerationStatusType;
import main.model.entities.enums.SettingsValueType;
import main.responses.BlogDTO;
import main.responses.CalendarResponseDTO;
import main.responses.CollectionTagsResponseDTO;
import main.responses.TagDTO;
import main.services.GlobalSettingsService;
import main.services.PostService;
import main.services.PostVoteService;
import main.services.TagService;
import main.servlet.AuthorizeServlet;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class ApiGeneralController {
    private AuthorizeServlet authorizeServlet;
    private GlobalSettingsService globalSettingsService;
    private PostService postService;
    private PostVoteService postVoteService;
    private TagService tagService;

    @Autowired
    public ApiGeneralController(AuthorizeServlet authorizeServlet, GlobalSettingsService globalSettingsService,
                                PostService postService, PostVoteService postVoteService, TagService tagService) {
        this.authorizeServlet = authorizeServlet;
        this.globalSettingsService = globalSettingsService;
        this.postService = postService;
        this.postVoteService = postVoteService;
        this.tagService = tagService;
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

    @GetMapping(value = "/api/calendar")
    public ResponseEntity<CalendarResponseDTO> getCalendar(@RequestParam(value = "year", required = false) Integer year) {
        if (year == null) {
            year = LocalDateTime.now().getYear();
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
    public ResponseEntity uploadImage(@RequestPart(value = "image", required = false) MultipartFile file) {
        String name = file.getOriginalFilename();
        StringBuilder mainPath = new StringBuilder("src/main/resources/upload/");
        String fileName = getRandomFileName(mainPath);
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(mainPath.toString())));
                stream.write(bytes);
                stream.close();
                return new ResponseEntity(fileName, HttpStatus.OK);
            } catch (Exception e) {
                String result = "Вам не удалось загрузить " + name + " => " + e.getMessage();
                return new ResponseEntity(result, HttpStatus.BAD_REQUEST);
            }
        } else {
            String result = "Вам не удалось загрузить " + name + " потому что файл пустой.";
            return new ResponseEntity(result, HttpStatus.NOT_FOUND);
        }
    }

    //==================================================================================================================

    private String getRandomFileName(StringBuilder mainPath) {
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
        mainPath.append(".jpg");
        fileName.append(".jpg");
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
}
