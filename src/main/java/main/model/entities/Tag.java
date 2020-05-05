package main.model.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tags")
@NoArgsConstructor
@Data
@ToString(exclude = {"posts"})
@EqualsAndHashCode(exclude = {"posts"})
public class Tag implements Serializable {

    private int id;
    private String name;
    private Set<Tag2Post> posts;

    //==============================================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "tag", fetch = FetchType.LAZY)
    public Set<Tag2Post> getPosts() {
        return posts;
    }

}
