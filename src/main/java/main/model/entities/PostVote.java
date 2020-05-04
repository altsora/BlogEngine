package main.model.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "post_votes")
@NoArgsConstructor
@Data
@ToString
public class PostVote implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "time", nullable = false)
    private Date time;

    @Column(name = "value", nullable = false)
    private int value;

    //==============================================================================

    @JsonBackReference
    public User getUser() {
        return user;
    }

    @JsonBackReference
    public Post getPost() {
        return post;
    }
}
