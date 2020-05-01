package main.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "post_comments")
@NoArgsConstructor
@Data
@ToString
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Post parent;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Post post;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;

    @Column(name = "time", nullable = false)
    private Date time;

    @Column(name = "text", nullable = false)
    private String text;
}
