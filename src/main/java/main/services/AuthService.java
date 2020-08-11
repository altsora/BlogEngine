package main.services;

public interface AuthService {
    boolean isUserAuthorize();

    long getAuthorizedUserId();

    void authorizeUser(long userId);

    void removeAuthorizedUser();
}
