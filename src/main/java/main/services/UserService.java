package main.services;

import main.model.entities.User;
import org.json.simple.JSONObject;

public interface UserService {
    User add(User user);

    User findByEmailAndPassword(String email, String password);

    User update(User updatedUser);

    boolean emailExists(String email);

    User findById(long userId);

    boolean nameIsInvalid(String name, JSONObject errors);

    boolean passwordIsInvalid(String password, JSONObject errors);

    boolean emailIsInvalid(String email, JSONObject errors);

    User findByCode(String code);
}
