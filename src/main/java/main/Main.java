package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@PropertySources({
        @PropertySource(value = "classpath:config/application.yml"),
        @PropertySource(value = "classpath:config/blog.properties"),
        @PropertySource(value = "classpath:config/captcha.properties")
})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}