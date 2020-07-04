package main.controller;

import lombok.RequiredArgsConstructor;
import main.model.entities.CaptchaCode;
import main.model.entities.User;
import main.model.enums.ActivityStatus;
import main.responses.LoginForm;
import main.responses.UserLoginDTO;
import main.services.CaptchaCodeService;
import main.services.PostService;
import main.services.UserService;
import main.servlet.AuthorizeServlet;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthorizeServlet authorizeServlet;
    private final CaptchaCodeService captchaCodeService;
    private final PostService postService;
    private final UserService userService;

    //==================================================================================================================

    @GetMapping(value = "/api/auth/check")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> authCheck() {
        JSONObject response = new JSONObject();
        boolean result;
        if (authorizeServlet.isUserAuthorize()) {
            long userId = authorizeServlet.getAuthorizedUserId();
            User userRep = userService.findById(userId);
            boolean userIsModerator = userRep.isModerator();
            int moderationCount = userIsModerator ? postService.getTotalCountOfNewPosts(ActivityStatus.ACTIVE) : 0;

            UserLoginDTO userLogin = UserLoginDTO.builder()
                    .id(userId)
                    .name(userRep.getName())
                    .photo(userRep.getPhoto())
                    .email(userRep.getEmail())
                    .moderation(userIsModerator)
                    .moderationCount(moderationCount)
                    .settings(userIsModerator)
                    .build();
            response.put("user", userLogin);
            result = true;
        } else {
            result = false;
        }
        response.put("result", result);

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/api/auth/login")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> login(@RequestBody LoginForm loginForm) {
        JSONObject response = new JSONObject();
        User userRep = userService.findByEmailAndPassword(loginForm.getE_mail(), loginForm.getPassword());
        boolean result;
        if (userRep != null) {
            long userId = userRep.getId();
            boolean userIsModerator = userRep.isModerator();
            int moderationCount = userIsModerator ? postService.getTotalCountOfNewPosts(ActivityStatus.ACTIVE) : 0;

            UserLoginDTO userLogin = UserLoginDTO.builder()
                    .id(userId)
                    .name(userRep.getName())
                    .photo(userRep.getPhoto())
                    .email(userRep.getEmail())
                    .moderation(userIsModerator)
                    .moderationCount(moderationCount)
                    .settings(userIsModerator)
                    .build();

            response.put("user", userLogin);
            authorizeServlet.authorizeUser(userId);
            result = true;
        } else {
            result = false;
        }
        response.put("result", result);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/api/auth/logout")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> logout() {
        authorizeServlet.removeAuthorizedUser();
        JSONObject response = new JSONObject();
        response.put("result", true);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/api/auth/register")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> registration(@RequestBody JSONObject request) {
        String email = (String) request.get("e_mail");
        String name = (String) request.get("name");
        String password = (String) request.get("password");
        String inputCaptchaCode = (String) request.get("captcha");
        String secretCode = (String) request.get("captcha_secret");

        boolean result = true;
        JSONObject response = new JSONObject();
        JSONObject errors = new JSONObject();

        if (userService.emailIsInvalid(email, errors)) {
            result = false;
        }

        if (userService.nameIsInvalid(name, errors)) {
            result = false;
        }

        if (userService.passwordIsInvalid(password, errors)) {
            result = false;
        }

        if (captchaCodeService.isIncorrectCaptcha(inputCaptchaCode, secretCode)) {
            errors.put("captcha", "Код с картинки введён неверно");
            result = false;
        }

        response.put("result", result);
        if (result) {
            userService.add(name, email, password);
        } else {
            response.put("errors", errors);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/api/auth/captcha")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> getCaptcha() {
        captchaCodeService.checkLifetimeCaptcha();
        CaptchaCode captcha = captchaCodeService.generateCaptcha();
        String code = captcha.getCode();
        String secretCode = captcha.getSecretCode();
        String imageCode = captchaCodeService.getCaptchaImageCode(code);
        JSONObject response = new JSONObject();
        response.put("secret", secretCode);
        response.put("image", "data:image/png;base64," + imageCode);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/api/auth/password")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> changePassword(@RequestBody JSONObject request) {
        String code = (String) request.get("code");
        String password = (String) request.get("password");
        String inputCaptchaCode = (String) request.get("captcha");
        String secretCode = (String) request.get("captcha_secret");

        boolean result = true;
        JSONObject response = new JSONObject();
        JSONObject errors = new JSONObject();

        User user = userService.findByCode(code);

        if (user == null) {
            errors.put("code", "Ссылка для восстановления пароля устарела.\n" +
                    "<a href=\"/auth/restore\">Запросить ссылку снова</a>");
            result = false;
        }
        if (userService.passwordIsInvalid(password, errors)) {
            result = false;
        }

        if (captchaCodeService.isIncorrectCaptcha(inputCaptchaCode, secretCode)) {
            errors.put("captcha", "Код с картинки введён неверно");
            result = false;
        }

        response.put("result", result);
        if (result) {
            user.setPassword(password);
        } else {
            response.put("errors", errors);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/api/auth/restore")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> restorePassword(@RequestBody JSONObject request) {
        //TODO: Недостаточно данных
        String email = (String) request.get("email");
        boolean result = userService.emailExists(email);
        JSONObject response = new JSONObject();
        response.put("result", result);
        return ResponseEntity.ok(response);
    }
}