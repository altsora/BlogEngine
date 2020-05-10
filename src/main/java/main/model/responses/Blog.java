package main.model.responses;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Blog {
    private String title = "DevPub";
    private String subtitle = "Рассказы разработчиков";
    private String phone = "+7 903 666-44-55";
    private String email = "mail@mail.ru";
    private String copyright = "Дмитрий Сергеев";
    private String copyrightFrom = "2005";
}
