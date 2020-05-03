package main.model.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "post_comments")
@NoArgsConstructor
@Data
@ToString(exclude = {"children"})
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PostComment parent;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Post post;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;

    @Column(name = "time", nullable = false)
    private Date time;

    @Column(name = "text", nullable = false)
    private String text;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<PostComment> children;
}
