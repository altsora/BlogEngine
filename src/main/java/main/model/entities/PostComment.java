package main.model.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "post_comments")
@NoArgsConstructor
@Data
@ToString(exclude = {"children"})
@EqualsAndHashCode(exclude = {"children"})
public class PostComment implements Serializable {

    private long id;
    private PostComment parent;
    private Post post;
    private User user;
    private LocalDateTime time;
    private String text;
    private Set<PostComment> children;

    //==============================================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    public PostComment getParent() {
        return parent;
    }

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    public Post getPost() {
        return post;
    }

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    @Column(name = "time", nullable = false)
    public LocalDateTime getTime() {
        return time;
    }

    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    public String getText() {
        return text;
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<PostComment> getChildren() {
        return children;
    }
}
