package main.model.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "post_votes")
@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class PostVote implements Serializable {

    private int id;
    private User user;
    private Post post;
    private LocalDateTime time;
    private byte value;

    //==============================================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    public Post getPost() {
        return post;
    }

    @Column(name = "time", nullable = false)
    public LocalDateTime getTime() {
        return time;
    }

    @Column(name = "value", nullable = false)
    public byte getValue() {
        return value;
    }

}
