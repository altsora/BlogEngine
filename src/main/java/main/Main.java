package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

//@Configuration
//@EnableAutoConfiguration
//@ComponentScan
@SpringBootApplication
@PropertySource(value = "classpath:application.yml")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
