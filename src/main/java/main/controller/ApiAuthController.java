package main.controller;

import main.model.entities.CaptchaCode;
import main.model.entities.User;
import main.model.enums.ActivesType;
import main.responses.LoginForm;
import main.responses.UserLoginDTO;
import main.services.CaptchaCodeService;
import main.services.PostService;
import main.services.UserService;
import main.servlet.AuthorizeServlet;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
public class ApiAuthController {
    private AuthorizeServlet authorizeServlet;
    private CaptchaCodeService captchaCodeService;
    private PostService postService;
    private UserService userService;

    @Autowired
    public ApiAuthController(AuthorizeServlet authorizeServlet, CaptchaCodeService captchaCodeService,
                             PostService postService, UserService userService) {
        this.authorizeServlet = authorizeServlet;
        this.captchaCodeService = captchaCodeService;
        this.postService = postService;
        this.userService = userService;
    }

    //==================================================================================================================

    @GetMapping(value = "/api/auth/check")
    public ResponseEntity<JSONObject> authCheck() {
        JSONObject response = new JSONObject();
        boolean result;
        if (authorizeServlet.isUserAuthorize()) {
            long userId = authorizeServlet.getAuthorizedUserId();
            User userRep = userService.findById(userId);
            UserLoginDTO userLoginDTO = new UserLoginDTO();
            userLoginDTO.setId(userId);
            userLoginDTO.setName(userRep.getName());
            userLoginDTO.setPhoto(userRep.getPhoto());
            userLoginDTO.setEmail(userRep.getEmail());
            boolean moderation = userRep.getIsModerator() == 1;
            userLoginDTO.setModeration(moderation);
            int moderationCount = moderation ? postService.getTotalCountOfNewPosts(ActivesType.ACTIVE) : 0;
            userLoginDTO.setModerationCount(moderationCount);
            userLoginDTO.setSettings(moderation);
            response.put("user", userLoginDTO);

            result = true;
        } else {
            result = false;
        }
        response.put("result", result);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/api/auth/login")
    @ResponseBody
    public ResponseEntity<JSONObject> login(@RequestBody LoginForm loginForm) {
        JSONObject response = new JSONObject();
        User userRep = userService.findByEmailAndPassword(loginForm.getE_mail(), loginForm.getPassword());
        boolean result;
        if (userRep != null) {
            long userId = userRep.getId();
            UserLoginDTO userLoginDTO = new UserLoginDTO();
            userLoginDTO.setId(userId);
            userLoginDTO.setName(userRep.getName());
            userLoginDTO.setPhoto(userRep.getPhoto());
            userLoginDTO.setEmail(userRep.getEmail());
            boolean moderation = userRep.getIsModerator() == 1;
            userLoginDTO.setModeration(moderation);
            int moderationCount = moderation ? postService.getTotalCountOfNewPosts(ActivesType.ACTIVE) : 0;
            userLoginDTO.setModerationCount(moderationCount);
            userLoginDTO.setSettings(moderation);
            result = true;
            response.put("user", userLoginDTO);

            authorizeServlet.authorizeUser(userId);
        } else {
            result = false;
        }
        response.put("result", result);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/auth/logout")
    @ResponseBody
    public ResponseEntity<JSONObject> logout() {
        authorizeServlet.removeAuthorizedUser();
        JSONObject response = new JSONObject();
        response.put("result", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/api/auth/register")
    public ResponseEntity registration(@RequestBody JSONObject request) {
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

        if (!captchaCodeService.checkCorrectCaptcha(inputCaptchaCode, secretCode)) {
            errors.put("captcha", "Код с картинки введён неверно");
            result = false;
        }

        response.put("result", result);
        if (result) {
            User user = new User();
            user.setRegTime(LocalDateTime.now());
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            userService.add(user);
        } else {
            response.put("errors", errors);
        }

        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/auth/captcha")
    public ResponseEntity getCaptcha() {
        captchaCodeService.checkLifetimeCaptcha();
        CaptchaCode captcha = captchaCodeService.generateCaptcha();
        String code = captcha.getCode();
        String secretCode = captcha.getSecretCode();
        String imageCode = captchaCodeService.getCaptchaImageCode(code, "png");
        JSONObject response = new JSONObject();
        response.put("secret", secretCode);
        response.put("image", "data:image/png;base64," + imageCode);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PostMapping(value = "/api/auth/password")
    public ResponseEntity changePassword(@RequestBody JSONObject request) {
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

        if (!captchaCodeService.checkCorrectCaptcha(inputCaptchaCode, secretCode)) {
            errors.put("captcha", "Код с картинки введён неверно");
            result = false;
        }

        response.put("result", result);
        if (result) {
            user.setPassword(password);
        } else {
            response.put("errors", errors);
        }

        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PostMapping(value = "/api/auth/restore")
    public ResponseEntity restorePassword(@RequestBody JSONObject request) {
        //TODO: Доделать позже. Недостаточно данных
        String email = (String) request.get("email");
        boolean result = userService.emailExists(email);
        JSONObject response = new JSONObject();
        response.put("result", result);
        return ResponseEntity.ok(response);
    }
}