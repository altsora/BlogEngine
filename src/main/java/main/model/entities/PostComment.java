package main.model.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "post_comments")
@NoArgsConstructor
@Data
@ToString(exclude = {"children"})
public class PostComment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_id")
    private PostComment parent;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "time", nullable = false)
    private Date time;

    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private Set<PostComment> children;

    //==============================================================================

    @JsonBackReference
    public PostComment getParent() {
        return parent;
    }

    @JsonBackReference
    public Post getPost() {
        return post;
    }

    @JsonBackReference
    public User getUser() {
        return user;
    }

    @JsonManagedReference
    public Set<PostComment> getChildren() {
        return children;
    }
}
