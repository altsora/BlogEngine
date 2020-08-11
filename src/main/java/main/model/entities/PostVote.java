package main.model.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import main.model.enums.Rating;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_votes")
@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class PostVote implements Serializable {
    private long id;
    private User user;
    private Post post;
    private LocalDateTime time;
    private Rating value;

    //==============================================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    public Post getPost() {
        return post;
    }

    @Column(name = "time", nullable = false)
    public LocalDateTime getTime() {
        return time;
    }

    @Column(name = "value", nullable = false)
    @Enumerated(EnumType.STRING)
    public Rating getValue() {
        return value;
    }
}
