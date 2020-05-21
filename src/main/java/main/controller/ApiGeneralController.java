package main.controller;

import main.model.entities.enums.ActivesType;
import main.model.entities.enums.ModerationStatusType;
import main.model.responses.BlogDTO;
import main.model.responses.CalendarResponseDTO;
import main.model.services.PostService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Tuple;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class ApiGeneralController {
    private PostService postService;

    @Autowired
    public ApiGeneralController(PostService postService) {
        this.postService = postService;
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
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping(value = "/api/calendar")
    public CalendarResponseDTO getCalendar(@RequestParam(value = "year", required = false) Integer year) {
        if (year == null) {
            year = LocalDateTime.now().getYear();
        }
        Map<String, Long> datesAndCountPosts = postService.getDateAndCountPosts(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED, year);
        JSONObject posts = new JSONObject(datesAndCountPosts);
        List<Integer> years = postService.findAllYearsOfPublication(ActivesType.ACTIVE, ModerationStatusType.ACCEPTED);
        CalendarResponseDTO calendarResponseDTO = new CalendarResponseDTO();
        calendarResponseDTO.setYears(years);
        calendarResponseDTO.setPosts(posts);
        return calendarResponseDTO;
    }
}
