package main.controller;

import lombok.RequiredArgsConstructor;
import main.model.entity.CaptchaCode;
import main.model.entity.User;
import main.request.LoginForm;
import main.request.PasswordChangeForm;
import main.request.RegisterForm;
import main.response.ErrorsDTO;
import main.response.ResultDTO;
import main.response.UserLoginDTO;
import main.service.CaptchaCodeService;
import main.service.GlobalSettingsService;
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
    private final GlobalSettingsService globalSettingsService;
    private final PostService postService;
    private final UserService userService;

    //==================================================================================================================

    @GetMapping(value = "/api/auth/check")
    public ResponseEntity<ResultDTO> authCheck() {
        ResultDTO response = new ResultDTO();
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
            result = true;
            response.setUser(userLogin);
        }
        response.setResult(result);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/api/auth/login")
    public ResponseEntity<ResultDTO> login(@RequestBody LoginForm loginForm) {
        ResultDTO response = new ResultDTO();
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
            authorizeServlet.authorizeUser(userId);
            result = true;
            response.setUser(userLogin);
        }
        response.setResult(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/api/auth/logout")
    public ResponseEntity<ResultDTO> logout() {
        ResultDTO response = new ResultDTO(true);
        authorizeServlet.removeAuthorizedUser();
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/api/auth/register")
    @SuppressWarnings("unchecked")
    public ResponseEntity<ResultDTO> registration(@RequestBody RegisterForm registerForm) {
        if (!globalSettingsService.settingMultiUserModeIsEnabled()) {
            return ResponseEntity.notFound().build();
        }

        String email = registerForm.getEmail();
        String name = registerForm.getName();
        String password = registerForm.getPassword();
        String inputCaptchaCode = registerForm.getCaptcha();
        String secretCode = registerForm.getCaptchaSecret();

        boolean result = true;
//        JSONObject response = new JSONObject();
//        JSONObject errors = new JSONObject();

        ErrorsDTO errors = new ErrorsDTO();
        ResultDTO response = new ResultDTO();

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
//            errors.put(KEY_CAPTCHA, MESSAGE_CAPTCHA_INVALID);
            errors.setCaptcha(MESSAGE_CAPTCHA_INVALID);
            result = false;
        }

//        response.put(KEY_RESULT, result);
        response.setResult(result);
        if (result) {
            userService.add(name, email, password);
        } else {
//            response.put(KEY_ERRORS, errors);
            response.setErrors(errors);
        }

//        return ResponseEntity.ok(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/api/auth/captcha")
    public ResponseEntity<ResultDTO> getCaptcha() {
        captchaCodeService.checkLifetimeCaptcha();
        CaptchaCode captcha = captchaCodeService.generateCaptcha();
        String code = captcha.getCode();
        String secretCode = captcha.getSecretCode();
        String imageCode = captchaCodeService.getCaptchaImageCode(code);
        ResultDTO response = new ResultDTO();
        response.setImage(IMAGE_ENCODING + imageCode);
        response.setSecret(secretCode);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/api/auth/password")
    @SuppressWarnings("unchecked")
    public ResponseEntity<ResultDTO> changePassword(@RequestBody PasswordChangeForm passwordChangeForm) {
        String code = passwordChangeForm.getCode();
        String password = passwordChangeForm.getPassword();
        String inputCaptchaCode = passwordChangeForm.getCode();
        String secretCode = passwordChangeForm.getCaptchaSecret();

        boolean result = true;
//        JSONObject response = new JSONObject();
//        JSONObject errors = new JSONObject();

        ErrorsDTO errors = new ErrorsDTO();
        ResultDTO response = new ResultDTO();

        User user = userService.findByCode(code);

        if (user == null) {
//            errors.put(KEY_CODE, MESSAGE_OLD_LINK);
            errors.setCode(MESSAGE_OLD_LINK);
            result = false;
        }
        if (userService.passwordIsInvalid(password, errors)) {
            result = false;
        }

        if (captchaCodeService.isIncorrectCaptcha(inputCaptchaCode, secretCode)) {
//            errors.put(KEY_CAPTCHA, MESSAGE_CAPTCHA_INVALID);
            errors.setCaptcha(MESSAGE_CAPTCHA_INVALID);
            result = false;
        }

//        response.put(KEY_RESULT, result);
        response.setResult(result);
        if (result) {
            user.setPassword(password);
        } else {
//            response.put(KEY_ERRORS, errors);
            response.setErrors(errors);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/api/auth/restore")
    public ResponseEntity<ResultDTO> restorePassword(@RequestBody JSONObject request) {
        //TODO
        String email = (String) request.get("email");
        boolean result = userService.emailExists(email);
        return ResponseEntity.ok(new ResultDTO(result));
    }
}