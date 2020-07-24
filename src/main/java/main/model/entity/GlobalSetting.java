package main.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import main.model.enums.SettingsCode;
import main.model.enums.SettingsValue;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "global_settings")
@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class GlobalSetting implements Serializable {
    private long id;
    private SettingsCode code;
    private String name;
    private SettingsValue value;

    //==============================================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false)
    public SettingsCode getCode() {
        return code;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "value", nullable = false)
    public SettingsValue getValue() {
        return value;
    }
}
