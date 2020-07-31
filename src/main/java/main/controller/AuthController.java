package main.controller;

import lombok.RequiredArgsConstructor;
import main.model.entity.CaptchaCode;
import main.model.entity.User;
import main.request.LoginForm;
import main.request.PasswordChangeForm;
import main.request.RegisterForm;
import main.response.UserLoginDTO;
import main.service.CaptchaCodeService;
import main.service.PostService;
import main.service.UserService;
import main.servlet.AuthorizeServlet;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static main.model.enums.ActivityStatus.ACTIVE;
import static main.util.MessageUtil.*;

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
        boolean result = false;
        if (authorizeServlet.isUserAuthorize()) {
            long userId = authorizeServlet.getAuthorizedUserId();
            User userRep = userService.findById(userId);
            boolean userIsModerator = userRep.isModerator();
            int moderationCount = userIsModerator ? postService.getTotalCountOfNewPosts(ACTIVE) : 0;

            UserLoginDTO userLogin = UserLoginDTO.builder()
                    .id(userId)
                    .name(userRep.getName())
                    .photo(userRep.getPhoto())
                    .email(userRep.getEmail())
                    .moderation(userIsModerator)
                    .moderationCount(moderationCount)
                    .settings(userIsModerator)
                    .build();
            response.put(KEY_USER, userLogin);
            result = true;
        }
        response.put(KEY_RESULT, result);

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/api/auth/login")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> login(@RequestBody LoginForm loginForm) {
        JSONObject response = new JSONObject();
        String email = loginForm.getEmail();
        String password = loginForm.getPassword();
        User userRep = userService.findByEmailAndPassword(email, password);
        boolean result = false;
        if (userRep != null) {
            long userId = userRep.getId();
            boolean userIsModerator = userRep.isModerator();
            int moderationCount = userIsModerator ? postService.getTotalCountOfNewPosts(ACTIVE) : 0;

            UserLoginDTO userLogin = UserLoginDTO.builder()
                    .id(userId)
                    .name(userRep.getName())
                    .photo(userRep.getPhoto())
                    .email(userRep.getEmail())
                    .moderation(userIsModerator)
                    .moderationCount(moderationCount)
                    .settings(userIsModerator)
                    .build();

            response.put(KEY_USER, userLogin);
            authorizeServlet.authorizeUser(userId);
            result = true;
        }
        response.put(KEY_RESULT, result);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/api/auth/logout")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> logout() {
        authorizeServlet.removeAuthorizedUser();
        JSONObject response = new JSONObject();
        response.put(KEY_RESULT, true);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/api/auth/register")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> registration(@RequestBody RegisterForm registerForm) {
        String email = registerForm.getEmail();
        String name = registerForm.getName();
        String password = registerForm.getPassword();
        String inputCaptchaCode = registerForm.getCaptcha();
        String secretCode = registerForm.getCaptchaSecret();

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
            errors.put(KEY_CAPTCHA, CAPTCHA_INVALID);
            result = false;
        }

        response.put(KEY_RESULT, result);
        if (result) {
            userService.add(name, email, password);
        } else {
            response.put(KEY_ERRORS, errors);
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
        response.put(KEY_SECRET, secretCode);
        response.put(KEY_IMAGE, IMAGE_ENCODING + imageCode);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/api/auth/password")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> changePassword(@RequestBody PasswordChangeForm passwordChangeForm) {
        String code = passwordChangeForm.getCode();
        String password = passwordChangeForm.getPassword();
        String inputCaptchaCode = passwordChangeForm.getCode();
        String secretCode = passwordChangeForm.getCaptchaSecret();

        boolean result = true;
        JSONObject response = new JSONObject();
        JSONObject errors = new JSONObject();

        User user = userService.findByCode(code);

        if (user == null) {
            errors.put(KEY_CODE, OLD_LINK);
            result = false;
        }
        if (userService.passwordIsInvalid(password, errors)) {
            result = false;
        }

        if (captchaCodeService.isIncorrectCaptcha(inputCaptchaCode, secretCode)) {
            errors.put(KEY_CAPTCHA, CAPTCHA_INVALID);
            result = false;
        }

        response.put(KEY_RESULT, result);
        if (result) {
            user.setPassword(password);
        } else {
            response.put(KEY_ERRORS, errors);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/api/auth/restore")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> restorePassword(@RequestBody JSONObject request) {
        //TODO
        String email = (String) request.get("email");
        boolean result = userService.emailExists(email);
        JSONObject response = new JSONObject();
        response.put(KEY_RESULT, result);
        return ResponseEntity.ok(response);
    }
}