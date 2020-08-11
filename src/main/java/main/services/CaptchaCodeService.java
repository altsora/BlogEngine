package main.services;

import main.model.entities.CaptchaCode;

public interface CaptchaCodeService {
    boolean isIncorrectCaptcha(String captcha, String secretCode);

    void checkLifetimeCaptcha();

    CaptchaCode generateCaptcha();

    String getCaptchaImageCode(String code);
}