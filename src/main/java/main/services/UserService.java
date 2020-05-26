package main.services;

import main.model.entities.User;

public interface UserService {
    User findByEmailAndPassword(String email, String password);

    User findById(long userId);
}
