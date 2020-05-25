package main.model.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import main.model.entities.enums.SettingsCodeType;
import main.model.entities.enums.SettingsValueType;

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
    private SettingsCodeType code;
    private String name;
    private SettingsValueType value;

    //==============================================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false)
    public SettingsCodeType getCode() {
        return code;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "value", nullable = false)
    public SettingsValueType getValue() {
        return value;
    }

}
