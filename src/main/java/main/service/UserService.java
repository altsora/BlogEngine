package main.service;

import main.model.entity.User;
import main.response.ErrorsDTO;

public interface UserService {
    boolean emailExists(String email);

    boolean emailIsInvalid(String email, ErrorsDTO errors);

    boolean nameIsInvalid(String name, ErrorsDTO errors);

    boolean passwordIsInvalid(String password, ErrorsDTO errors);

    User add(String name, String email, String password);

    User findByCode(String code);

    User findByEmailAndPassword(String email, String password);

    User findById(long userId);

    User update(User updatedUser);
}
