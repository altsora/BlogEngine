package main.model.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "global_settings")
@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class GlobalSettings implements Serializable {

    private int id;
    private String code;
    private String name;
    private String value;

    //==============================================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    @Column(name = "code", nullable = false)
    public String getCode() {
        return code;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    @Column(name = "value", nullable = false)
    public String getValue() {
        return value;
    }

}
