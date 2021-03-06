package main.model.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "captcha_codes")
@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class CaptchaCode implements Serializable {
    private long id;
    private LocalDateTime time;
    private String code;
    private String secretCode;

    //==============================================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    @Column(name = "time", nullable = false)
    public LocalDateTime getTime() {
        return time;
    }

    @Column(name = "code", nullable = false, columnDefinition = "TINYTEXT")
    public String getCode() {
        return code;
    }

    @Column(name = "secret_code", nullable = false, columnDefinition = "TINYTEXT")
    public String getSecretCode() {
        return secretCode;
    }
}
