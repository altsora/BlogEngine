package main.services;

import main.api.responses.ErrorResponse;
import main.model.entities.User;

public interface UserService {
    boolean emailExists(String email);

    boolean emailIsInvalid(String email, ErrorResponse errors);

    boolean nameIsInvalid(String name, ErrorResponse errors);

    boolean passwordIsInvalid(String password, ErrorResponse errors);

    User add(String name, String email, String password);

    User findByCode(String code);

    User findByEmailAndPassword(String email, String password);

    User findById(long userId);

    User update(User updatedUser);
}
