package main.model.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Data
@ToString(exclude = {"posts", "modifiedPosts", "ratedPosts", "comments"})
@EqualsAndHashCode(exclude = {"posts", "modifiedPosts", "ratedPosts", "comments"})
public class User implements Serializable {

    private int id;
    private boolean isModerator;
    private Date regTime;
    private String name;
    private String email;
    private String password;
    private String code;
    private String photo;
    private Set<Post> posts;
    private Set<Post> modifiedPosts;
    private Set<PostVote> ratedPosts;
    private Set<PostComment> comments;

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
//
//    @Column(name = "is_moderator", nullable = false)
//    private boolean isModerator;
//
//    @Column(name = "reg_time", nullable = false)
//    private Date regTime;
//
//    @Column(name = "name", nullable = false)
//    private String name;
//
//    @Column(name = "email", nullable = false)
//    private String email;
//
//    @Column(name = "password", nullable = false)
//    private String password;
//
//    @Column(name = "code")
//    private String code;
//
//    @Column(name = "photo", columnDefinition = "TEXT")
//    private String photo;
//
//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
//    private Set<Post> posts;
//
//    @OneToMany(mappedBy = "moderator", fetch = FetchType.LAZY)
//    private Set<Post> modifiedPosts;
//
//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
//    private Set<PostVote> ratedPosts;
//
//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
//    private Set<PostComment> comments;

    //==============================================================================

//    @JsonManagedReference
//    public Set<Post> getPosts() {
//        return posts;
//    }
//
//    @JsonManagedReference
//    public Set<Post> getModifiedPosts() {
//        return modifiedPosts;
//    }
//
//    @JsonManagedReference
//    public Set<PostVote> getRatedPosts() {
//        return ratedPosts;
//    }
//
//    @JsonManagedReference
//    public Set<PostComment> getComments() {
//        return comments;
//    }

    //==============================================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    @Column(name = "is_moderator", nullable = false)
    public boolean isModerator() {
        return isModerator;
    }

    @Column(name = "reg_time", nullable = false)
    public Date getRegTime() {
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
    @OneToMany(mappedBy = "moderator", fetch = FetchType.LAZY)
    public Set<Post> getModifiedPosts() {
        return modifiedPosts;
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    public Set<PostVote> getRatedPosts() {
        return ratedPosts;
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    public Set<PostComment> getComments() {
        return comments;
    }
}
