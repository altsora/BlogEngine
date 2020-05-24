package main.controller;

import main.model.entities.User;
import main.responses.LoginForm;
import main.responses.UserLoginDTO;
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
    private UserService userService;

    @Autowired
    public ApiAuthController(AuthorizeServlet authorizeServlet, UserService userService) {
        this.authorizeServlet = authorizeServlet;
        this.userService = userService;
    }

    @GetMapping(value = "/api/auth/check")
    public ResponseEntity<JSONObject> authCheck() {
        JSONObject answer = new JSONObject();
        boolean result;
        if (authorizeServlet.isUserAuthorize()) {
            int userId = authorizeServlet.getAuthorizedUserId();
            User userRep = userService.findByUserId(userId);
            UserLoginDTO userLoginDTO = new UserLoginDTO();
            userLoginDTO.setId(userId);
            userLoginDTO.setName(userRep.getName());
            userLoginDTO.setPhoto(userRep.getPhoto());
            userLoginDTO.setEmail(userRep.getEmail());
            boolean moderation = userRep.getIsModerator() == 1;
            userLoginDTO.setModeration(moderation);
            userLoginDTO.setModerationCount(userRep.getModifiedPosts().size());
            userLoginDTO.setSettings(moderation);
            answer.put("user", userLoginDTO);

            result = true;
        } else {
            result = false;
        }
        answer.put("result", result);

        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @PostMapping(value = "/api/auth/login")
    @ResponseBody
    public ResponseEntity<JSONObject> login(@RequestBody LoginForm loginForm) {
        JSONObject answer = new JSONObject();
        User userRep = userService.findByEmailAndPassword(loginForm.getE_mail(), loginForm.getPassword());
        boolean result;
        if (userRep != null) {
            int userId = userRep.getId();
            UserLoginDTO userLoginDTO = new UserLoginDTO();
            userLoginDTO.setId(userId);
            userLoginDTO.setName(userRep.getName());
            userLoginDTO.setPhoto(userRep.getPhoto());
            userLoginDTO.setEmail(userRep.getEmail());
            boolean moderation = userRep.getIsModerator() == 1;
            userLoginDTO.setModeration(moderation);
            userLoginDTO.setModerationCount(userRep.getModifiedPosts().size());
            userLoginDTO.setSettings(moderation);
            result = true;
            answer.put("user", userLoginDTO);

            authorizeServlet.authorizeUser(userId);
        } else {
            result = false;
        }
        answer.put("result", result);

        return new ResponseEntity<>(answer, HttpStatus.OK);
    }
}