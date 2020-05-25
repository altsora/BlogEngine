package main.controller;

import main.model.entities.GlobalSetting;
import main.model.entities.enums.ActivesType;
import main.model.entities.enums.ModerationStatusType;
import main.model.entities.enums.SettingsValueType;
import main.responses.BlogDTO;
import main.responses.CalendarResponseDTO;
import main.services.GlobalSettingsService;
import main.services.PostService;
import main.services.PostVoteService;
import main.servlet.AuthorizeServlet;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
public class ApiGeneralController {
    private AuthorizeServlet authorizeServlet;
    private GlobalSettingsService globalSettingsService;
    private PostService postService;
    private PostVoteService postVoteService;

    @Autowired
    public ApiGeneralController(AuthorizeServlet authorizeServlet, GlobalSettingsService globalSettingsService,
                                PostService postService, PostVoteService postVoteService) {
        this.authorizeServlet = authorizeServlet;
        this.globalSettingsService = globalSettingsService;
        this.postService = postService;
        this.postVoteService = postVoteService;
    }

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
}
