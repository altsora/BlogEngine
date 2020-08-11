package main.utils;

public class MessageUtil {
    public static final String IMAGE_ENCODING = "data:image/png;base64,";

    public static final String MESSAGE_CAPTCHA_INVALID = "Код с картинки введён неверно";
    public static final String MESSAGE_COMMENT_NOT_FOUND = "Комментарий не существует";
    public static final String MESSAGE_COMMENT_SHORT = "Текст комментария не задан или слишком короткий";
    public static final String MESSAGE_EMAIL_EMPTY = "Укажите e-mail";
    public static final String MESSAGE_EMAIL_EXISTS = "Этот e-mail уже зарегистрирован";
    public static final String MESSAGE_IMAGE_ERROR_LOAD = "Не удалось загрузить изображение";
    public static final String MESSAGE_IMAGE_INVALID_FORMAT = "Изображение должно быть формата JPG/PNG";
    public static final String MESSAGE_NAME_EMPTY = "Укажите имя";
    public static final String MESSAGE_NAME_LENGTH = "Имя должно быть длиной от 3 до 30 символов";
    public static final String MESSAGE_OLD_LINK = "Ссылка для восстановления пароля устарела.\n" +
            "<a href=\\\"/auth/restore\\\">Запросить ссылку снова</a>";
    public static final String MESSAGE_PASSWORD_LONG = "Пароль слишком длинный";
    public static final String MESSAGE_PASSWORD_SHORT = "Пароль короче 6-ти символов";
    public static final String MESSAGE_POST_EMPTY = "Пост не должен быть пустым";
    public static final String MESSAGE_POST_NOT_FOUND = "Пост не существует";
    public static final String MESSAGE_POST_SHORT = "Минимальное количество символов в публикации - 50";
    public static final String MESSAGE_TITLE_EMPTY = "Заголовок не должен быть пустым";
    public static final String MESSAGE_TITLE_SHORT = "Минимальное количество символов в заголовке - 3";

    private MessageUtil() {}
}