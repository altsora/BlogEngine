package main.services.impl;

import lombok.RequiredArgsConstructor;
import main.api.responses.ErrorsDTO;
import main.model.entities.User;
import main.repositories.UserRepository;
import main.services.UserService;
import main.utils.TimeUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static main.utils.MessageUtil.*;

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
    public boolean nameIsInvalid(String name, ErrorsDTO errors) {
        if (name == null || name.isEmpty()) {
            errors.setName(MESSAGE_NAME_EMPTY);
            return true;
        }

        if (name.length() < 3 || name.length() > 30) {
            errors.setName(MESSAGE_NAME_LENGTH);
            return true;
        }

        return false;
    }

    @Override
    public boolean passwordIsInvalid(String password, ErrorsDTO errors) {
        if (password.length() < 6) {
            errors.setPassword(MESSAGE_PASSWORD_SHORT);
            return true;
        }

        if (password.length() > 50) {
            errors.setPassword(MESSAGE_PASSWORD_LONG);
            return true;
        }
        return false;
    }

    @Override
    public boolean emailIsInvalid(String email, ErrorsDTO errors) {
        if (emailExists(email)) {
            errors.setEmail(MESSAGE_EMAIL_EXISTS);
            return true;
        }
        return false;
    }

    @Override
    public User findByCode(String code) {
        return userRepository.findByCode(code);
    }
}