package main.util;

public class MessageUtil {
    public static final String KEY_CAPTCHA = "captcha";
    public static final String KEY_CODE = "code";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ERRORS = "errors";
    public static final String KEY_ID = "id";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_NAME = "name";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_RESULT = "result";
    public static final String KEY_SECRET = "secret";
    public static final String KEY_TEXT = "text";
    public static final String KEY_USER = "user";

    public static final String IMAGE_ENCODING = "data:image/png;base64,";
    public static final String OLD_LINK = "Ссылка для восстановления пароля устарела.\n" +
            "<a href=\\\"/auth/restore\\\">Запросить ссылку снова</a>";

    public static final String INVALID_CAPTCHA = "Код с картинки введён неверно";
    public static final String EMAIL_EXISTS = "Этот e-mail уже зарегистрирован";
    public static final String NAME_EMPTY = "Укажите имя";
    public static final String NAME_LENGTH = "Имя должно быть длиной от 3 до 30 символов";
    public static final String COMMENT_NOT_FOUND = "Комментарий не существует";
    public static final String POST_NOT_FOUND = "Пост не существует";
    public static final String TEXT_COMMENT = "Текст комментария не задан или слишком короткий";
    public static final String PASSWORD_LONG = "Пароль слишком длинный";
    public static final String PASSWORD_SHORT = "Пароль короче 6-ти символов";

}
