package main.controller;

import lombok.RequiredArgsConstructor;
import main.services.impl.CaptchaCodeServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final CaptchaCodeServiceImpl captchaCodeService;

    @GetMapping(value = "/api/test")
    @ResponseBody
    public void testMethod2() {
    }
}


