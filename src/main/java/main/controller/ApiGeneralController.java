package main.controller;

import main.model.responses.Blog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiGeneralController {

    @GetMapping(value = "/api/init")
    @ResponseBody
    public Blog init() {
        Blog blog = new Blog();
        blog.setTitle("DevPub");
        blog.setSubtitle("Рассказы разработчиков");
        blog.setPhone("+7 903 666-44-55");
        blog.setEmail("mail@mail.ru");
        blog.setCopyright("Дмитрий Сергеев");
        blog.setCopyrightFrom("2005");
        return blog;
    }

    @GetMapping(value = "/api/settings")
    public ResponseEntity getSettings() {
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
