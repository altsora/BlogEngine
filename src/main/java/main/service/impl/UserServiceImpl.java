package main.service.impl;

import lombok.RequiredArgsConstructor;
import main.model.entity.User;
import main.repository.UserRepository;
import main.service.UserService;
import main.util.TimeUtil;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static main.util.MessageUtil.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    //==================================================================================================================

    @Override
    public User add(String name, String email, String password) {
        User user = new User();
        user.setRegTime(LocalDateTime.now(TimeUtil.TIME_ZONE));
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
        if (name == null || name.isEmpty()) {
            errors.put(KEY_NAME, MESSAGE_NAME_EMPTY);
            return true;
        }

        if (name.length() < 3 || name.length() > 30) {
            errors.put(KEY_NAME, MESSAGE_NAME_LENGTH);
            return true;
        }

        return false;
    }

    @Override
    public boolean passwordIsInvalid(String password, JSONObject errors) {
        if (password.length() < 6) {
            errors.put(KEY_PASSWORD, MESSAGE_PASSWORD_SHORT);
            return true;
        }

        if (password.length() > 50) {
            errors.put(KEY_PASSWORD, MESSAGE_PASSWORD_LONG);
            return true;
        }
        return false;
    }

    @Override
    public boolean emailIsInvalid(String email, JSONObject errors) {
        if (emailExists(email)) {
            errors.put(KEY_EMAIL, MESSAGE_EMAIL_EXISTS);
            return true;
        }
        return false;
    }

    @Override
    public User findByCode(String code) {
        return userRepository.findByCode(code);
    }
}
