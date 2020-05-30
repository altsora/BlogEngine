package main.services.impl;

import main.model.entities.User;
import main.repositories.UserRepository;
import main.services.UserService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //==================================================================================================================

    @Override
    public User add(User user) {
        return userRepository.saveAndFlush(user);
    }

    @Override
    public User findByEmailAndPassword(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }

    @Override
    public User update(User updatedUser) {
        return userRepository.saveAndFlush(updatedUser);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Override
    public User findById(long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public boolean nameIsInvalid(String name, JSONObject errors) {
        String key = "name";
        if (name.isEmpty()) {
            errors.put(key, "Укажите имя");
            return true;
        }

        if (name.length() < 3 || name.length() > 30) {
            errors.put(key, "Имя должно быть длиной от 3 до 30 символов");
            return true;
        }

        String containsNumbers = ".*\\d.*";
        if (name.matches(containsNumbers)) {
            errors.put(key, "Имя не должно содержать числа");
            return true;
        }
        return false;
    }

    @Override
    public boolean passwordIsInvalid(String password, JSONObject errors) {
        String key = "password";
        if (password.length() < 6) {
            errors.put(key, "Пароль короче 6-ти символов");
            return true;
        }

        if (password.length() > 50) {
            errors.put(key, "Максимальная длина пароля - 50 символов");
            return true;
        }
        return false;
    }

    @Override
    public boolean emailIsInvalid(String email, JSONObject errors) {
        String key = "email";
        if (emailExists(email)) {
            errors.put(key, "Этот e-mail уже зарегистрирован");
            return true;
        }

        String containsIncorrectSymbols = "\\W";
        if (email.matches(containsIncorrectSymbols)) {
            errors.put(key, "Может содержать только буквенный или цифровой символ или знак подчёркивания");
            return true;
        }
        return false;
    }
}
