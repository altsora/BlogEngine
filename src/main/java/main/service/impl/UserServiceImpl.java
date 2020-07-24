package main.service.impl;

import lombok.RequiredArgsConstructor;
import main.model.entity.User;
import main.repository.UserRepository;
import main.service.UserService;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    //==================================================================================================================

    @Override
    public User add(String name, String email, String password) {
        User user = new User();
        user.setRegTime(LocalDateTime.now(ZoneId.of("UTC")));
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
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
        return false;
    }

    @Override
    public User findByCode(String code) {
        return userRepository.findByCode(code);
    }
}
