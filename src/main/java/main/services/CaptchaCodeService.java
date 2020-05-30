package main.services;

import main.model.entities.CaptchaCode;

public interface CaptchaCodeService {
    CaptchaCode generateCaptcha();

    String getCaptchaImageCode(String code, String formatName);

    boolean checkCorrectCaptcha(String captcha, String secretCode);

    void checkLifetimeCaptcha();
}