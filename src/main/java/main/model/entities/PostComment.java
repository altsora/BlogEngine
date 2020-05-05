package main.model.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "post_comments")
@NoArgsConstructor
@Data
@ToString(exclude = {"children"})
@EqualsAndHashCode(exclude = {"children"})
public class PostComment implements Serializable {

    private int id;
    private PostComment parent;
    private Post post;
    private User user;
    private Date time;
    private String text;
    private Set<PostComment> children;

    //==============================================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    public PostComment getParent() {
        return parent;
    }

    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    public Post getPost() {
        return post;
    }

    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    @Column(name = "time", nullable = false)
    public Date getTime() {
        return time;
    }

    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    public String getText() {
        return text;
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    public Set<PostComment> getChildren() {
        return children;
    }


//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
//
//    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "parent_id")
//    private PostComment parent;
//
//    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "post_id")
//    private Post post;
//
//    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    @Column(name = "time", nullable = false)
//    private Date time;
//
//    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
//    private String text;
//
//    @JsonManagedReference
//    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
//    private Set<PostComment> children;
//
//    //==============================================================================
//
//    @JsonBackReference
//    public PostComment getParent() {
//        return parent;
//    }
//
//    @JsonBackReference
//    public Post getPost() {
//        return post;
//    }
//
//    @JsonBackReference
//    public User getUser() {
//        return user;
//    }
//
//    @JsonManagedReference
//    public Set<PostComment> getChildren() {
//        return children;
//    }
}
