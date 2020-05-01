package main.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "post_votes")
@NoArgsConstructor
@Data
@ToString
public class PostVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "post_id", nullable = false)
    private int postId;

    @Column(name = "time", nullable = false)
    private Date time;

    @Column(name = "value", nullable = false)
    private int value;
}
