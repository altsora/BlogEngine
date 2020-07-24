package main.service;

import main.model.entity.CaptchaCode;

public interface CaptchaCodeService {
    CaptchaCode generateCaptcha();

    String getCaptchaImageCode(String code);

    boolean isIncorrectCaptcha(String captcha, String secretCode);

    void checkLifetimeCaptcha();
}