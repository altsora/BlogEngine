package main.controller;

import main.model.entities.User;
import main.model.entities.enums.ActivesType;
import main.responses.LoginForm;
import main.responses.UserLoginDTO;
import main.services.PostService;
import main.services.UserService;
import main.servlet.AuthorizeServlet;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiAuthController{
    private AuthorizeServlet authorizeServlet;
    private PostService postService;
    private UserService userService;

    @Autowired
    public ApiAuthController(AuthorizeServlet authorizeServlet, PostService postService, UserService userService) {
        this.authorizeServlet = authorizeServlet;
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
        return new ResponseEntity<JSONObject>(response, HttpStatus.OK);
    }
}