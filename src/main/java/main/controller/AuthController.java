package main.controller;

import lombok.RequiredArgsConstructor;
import main.model.entity.CaptchaCode;
import main.model.entity.User;
import main.model.enums.ActivityStatus;
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
//        return ResponseEntity.ok(null);
    }

    @PostMapping(value = "/api/auth/login")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> login(@RequestBody LoginForm loginForm) {
        JSONObject response = new JSONObject();
        User userRep = userService.findByEmailAndPassword(loginForm.getEmail(), loginForm.getPassword());
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

//    @PostMapping(value = "/api/auth/register")
//    @SuppressWarnings("unchecked")
//    public ResponseEntity<JSONObject> registration(@RequestBody RegisterForm registerForm) {
//        String email = registerForm.getEmail();
//        String name = registerForm.getName();
//        String password = registerForm.getPassword();
//        String inputCaptchaCode = registerForm.getCaptcha();
//        String secretCode = registerForm.getCaptchaSecret();
//
//        boolean result = true;
//        JSONObject response = new JSONObject();
//        JSONObject errors = new JSONObject();
//
//        if (userService.emailIsInvalid(email, errors)) {
//            result = false;
//        }
//
//        if (userService.nameIsInvalid(name, errors)) {
//            result = false;
//        }
//
//        if (userService.passwordIsInvalid(password, errors)) {
//            result = false;
//        }
//
//        if (captchaCodeService.isIncorrectCaptcha(inputCaptchaCode, secretCode)) {
//            errors.put("captcha", "Код с картинки введён неверно");
//            result = false;
//        }
//
//        response.put("result", result);
//        if (result) {
//            userService.add(name, email, password);
//        } else {
//            response.put("errors", errors);
//        }
//
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping(value = "/api/auth/captcha")
//    @SuppressWarnings("unchecked")
//    public ResponseEntity<JSONObject> getCaptcha() {
//        captchaCodeService.checkLifetimeCaptcha();
//        CaptchaCode captcha = captchaCodeService.generateCaptcha();
//        String code = captcha.getCode();
//        String secretCode = captcha.getSecretCode();
//        String imageCode = captchaCodeService.getCaptchaImageCode(code);
//        JSONObject response = new JSONObject();
//        response.put("secret", secretCode);
//        response.put("image", "data:image/png;base64," + imageCode);
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping(value = "/api/auth/password")
//    @SuppressWarnings("unchecked")
//    public ResponseEntity<JSONObject> changePassword(@RequestBody PasswordChangeForm passwordChangeForm) {
//        String code = passwordChangeForm.getCode();
//        String password = passwordChangeForm.getPassword();
//        String inputCaptchaCode = passwordChangeForm.getCode();
//        String secretCode = passwordChangeForm.getCaptchaSecret();
//
//        boolean result = true;
//        JSONObject response = new JSONObject();
//        JSONObject errors = new JSONObject();
//
//        User user = userService.findByCode(code);
//
//        if (user == null) {
//            errors.put("code", "Ссылка для восстановления пароля устарела.\n" +
//                    "<a href=\"/auth/restore\">Запросить ссылку снова</a>");
//            result = false;
//        }
//        if (userService.passwordIsInvalid(password, errors)) {
//            result = false;
//        }
//
//        if (captchaCodeService.isIncorrectCaptcha(inputCaptchaCode, secretCode)) {
//            errors.put("captcha", "Код с картинки введён неверно");
//            result = false;
//        }
//
//        response.put("result", result);
//        if (result) {
//            user.setPassword(password);
//        } else {
//            response.put("errors", errors);
//        }
//
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping(value = "/api/auth/restore")
//    @SuppressWarnings("unchecked")
//    public ResponseEntity<JSONObject> restorePassword(@RequestBody JSONObject request) {
//        //TODO: 1
//        String email = (String) request.get("email");
//        boolean result = userService.emailExists(email);
//        JSONObject response = new JSONObject();
//        response.put("result", result);
//        return ResponseEntity.ok(response);
//    }
}