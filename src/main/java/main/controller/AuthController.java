package main.controller;

import lombok.RequiredArgsConstructor;
import main.api.requests.LoginForm;
import main.api.requests.PasswordChangeForm;
import main.api.requests.RegisterForm;
import main.api.responses.AbstractResponse;
import main.api.responses.ErrorResponse;
import main.api.responses.ResultResponse;
import main.model.entities.CaptchaCode;
import main.model.entities.User;
import main.services.AuthService;
import main.services.CaptchaCodeService;
import main.services.GlobalSettingsService;
import main.services.UserService;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static main.utils.MessageUtil.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final CaptchaCodeService captchaCodeService;
    private final GlobalSettingsService globalSettingsService;
    private final UserService userService;

    //==================================================================================================================

    @GetMapping(value = "/check")
    public ResponseEntity<AbstractResponse> authCheck() {
        ResultResponse response = new ResultResponse();
        boolean result = false;
        if (authService.isUserAuthorize()) {
            long userId = authService.getAuthorizedUserId();
            User userRep = userService.findById(userId);
            result = true;
            response.setUser(userService.createUserLogin(userRep));
        }
        response.setResult(result);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<AbstractResponse> login(@RequestBody LoginForm loginForm) {
        ResultResponse response = new ResultResponse();
        String email = loginForm.getEmail();
        String password = loginForm.getPassword();
        User userRep = userService.findByEmailAndPassword(email, password);
        boolean result = false;
        if (userRep != null) {
            long userId = userRep.getId();
            authService.authorizeUser(userId);
            result = true;
            response.setUser(userService.createUserLogin(userRep));
        }
        response.setResult(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/logout")
    public ResponseEntity<AbstractResponse> logout() {
        ResultResponse response = new ResultResponse(true);
        authService.removeAuthorizedUser();
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<AbstractResponse> registration(@RequestBody RegisterForm registerForm) {
        if (!globalSettingsService.settingMultiUserModeIsEnabled()) {
            return ResponseEntity.notFound().build();
        }

        String email = registerForm.getEmail();
        String name = registerForm.getName();
        String password = registerForm.getPassword();
        String inputCaptchaCode = registerForm.getCaptcha();
        String secretCode = registerForm.getCaptchaSecret();

        boolean result = true;
        ErrorResponse errors = new ErrorResponse();
        ResultResponse response = new ResultResponse();

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
            errors.setCaptcha(MESSAGE_CAPTCHA_INVALID);
            result = false;
        }

        response.setResult(result);
        if (result) {
            userService.add(name, email, password);
        } else {
            response.setErrors(errors);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/captcha")
    public ResponseEntity<AbstractResponse> getCaptcha() {
        captchaCodeService.checkLifetimeCaptcha();
        CaptchaCode captcha = captchaCodeService.generateCaptcha();
        String code = captcha.getCode();
        String secretCode = captcha.getSecretCode();
        String imageCode = captchaCodeService.getCaptchaImageCode(code);
        ResultResponse response = new ResultResponse();
        response.setImage(IMAGE_ENCODING + imageCode);
        response.setSecret(secretCode);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/password")
    public ResponseEntity<AbstractResponse> changePassword(@RequestBody PasswordChangeForm passwordChangeForm) {
        String code = passwordChangeForm.getCode();
        String password = passwordChangeForm.getPassword();
        String inputCaptchaCode = passwordChangeForm.getCode();
        String secretCode = passwordChangeForm.getCaptchaSecret();

        boolean result = true;
        ErrorResponse errors = new ErrorResponse();
        ResultResponse response = new ResultResponse();

        User user = userService.findByCode(code);

        if (user == null) {
            errors.setCode(MESSAGE_OLD_LINK);
            result = false;
        }
        if (userService.passwordIsInvalid(password, errors)) {
            result = false;
        }

        if (captchaCodeService.isIncorrectCaptcha(inputCaptchaCode, secretCode)) {
            errors.setCaptcha(MESSAGE_CAPTCHA_INVALID);
            result = false;
        }

        response.setResult(result);
        if (result) {
            user.setPassword(password);
        } else {
            response.setErrors(errors);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/restore")
    public ResponseEntity<AbstractResponse> restorePassword(@RequestBody JSONObject request) {
        //TODO
        String email = (String) request.get("email");
        boolean result = userService.emailExists(email);
        return ResponseEntity.ok(new ResultResponse(result));
    }
}