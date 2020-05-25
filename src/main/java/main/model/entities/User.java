package main.model.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Data
@ToString(exclude = {"posts", "modifiedPosts", "ratedPosts", "comments"})
@EqualsAndHashCode(exclude = {"posts", "modifiedPosts", "ratedPosts", "comments"})
public class User implements Serializable {

    private long id;
    private byte isModerator;
    private LocalDateTime regTime;
    private String name;
    private String email;
    private String password;
    private String code;
    private String photo;
    private Set<Post> posts;
    private Set<Post> modifiedPosts;
    private Set<PostVote> ratedPosts;
    private Set<PostComment> comments;

    //==============================================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    @Column(name = "is_moderator", nullable = false)
    public byte getIsModerator() {
        return isModerator;
    }

    @Column(name = "reg_time", nullable = false)
    public LocalDateTime getRegTime() {
        return regTime;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    @Column(name = "email", nullable = false)
    public String getEmail() {
        return email;
    }

    @Column(name = "password", nullable = false)
    public String getPassword() {
        return password;
    }

    @Column(name = "code")
    public String getCode() {
        return code;
    }

    @Column(name = "photo", columnDefinition = "TEXT")
    public String getPhoto() {
        return photo;
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    public Set<Post> getPosts() {
        return posts;
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "moderator", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<Post> getModifiedPosts() {
        return modifiedPosts;
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<PostVote> getRatedPosts() {
        return ratedPosts;
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<PostComment> getComments() {
        return comments;
    }
}
