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
@EqualsAndHashCode(of = {"name"})
public class Tag implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "tag", fetch = FetchType.LAZY)
    private Set<Tag2Post> posts;

    //==============================================================================

    @JsonManagedReference
    public Set<Tag2Post> getPosts() {
        return posts;
    }
}
