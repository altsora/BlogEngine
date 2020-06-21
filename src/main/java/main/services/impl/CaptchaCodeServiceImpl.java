package main.services.impl;

import com.github.cage.YCage;
import lombok.RequiredArgsConstructor;
import main.model.entities.CaptchaCode;
import main.repositories.CaptchaCodeRepository;
import main.services.CaptchaCodeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class CaptchaCodeServiceImpl implements CaptchaCodeService {
    @Value("#{T(java.time.LocalDateTime).now(T(java.time.ZoneId).of(\"UTC\")).minusHours('${captcha.lifetime.hour}')}")
    private LocalDateTime captchaLifetime;
    private final int CAPTCHA_CODE_LENGTH = 3;
    private final int WIDTH = 100;
    private final int HEIGHT = 35;

    private final CaptchaCodeRepository captchaCodeRepository;

    //==================================================================================================================

    @Override
    public CaptchaCode generateCaptcha() {
        String code = generateCode();
        String secretCode = Base64.getEncoder().encodeToString(code.getBytes());
        CaptchaCode captcha = new CaptchaCode();
        captcha.setTime(LocalDateTime.now());
        captcha.setCode(code);
        captcha.setSecretCode(secretCode);
        return captchaCodeRepository.saveAndFlush(captcha);
    }

    @Override
    public String getCaptchaImageCode(String code) {
        BufferedImage image = new YCage().drawImage(code);
        if (image.getWidth() > WIDTH && image.getHeight() > HEIGHT) {
            int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
            BufferedImage resizeImage = new BufferedImage(WIDTH, HEIGHT, type);
            Graphics2D g = resizeImage.createGraphics();
            g.drawImage(image, 0, 0, WIDTH, HEIGHT, null);
            g.dispose();
            image = resizeImage;
        }
        byte[] imageBytes = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            imageBytes = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    @Override
    public boolean checkCorrectCaptcha(String captcha, String secretCode) {
        String correctCaptcha = captchaCodeRepository.getCodeBySecretCode(secretCode);
        return captcha.equals(correctCaptcha);
    }

    @Override
    public void checkLifetimeCaptcha() {
        List<CaptchaCode> list = captchaCodeRepository.findAll();
        for (CaptchaCode captcha : list) {
            if (captcha.getTime().isBefore(captchaLifetime)) {
                captchaCodeRepository.deleteById(captcha.getId());
            }
        }
    }

    //==================================================================================================================

    private String generateCode() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CAPTCHA_CODE_LENGTH; i++) {
            int index = (int) (Math.random() * alphabet.length());
            sb.append(alphabet.charAt(index));
        }
        return sb.toString();
    }


}
