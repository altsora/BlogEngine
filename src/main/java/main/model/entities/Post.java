package main.model.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import main.model.ModerationStatusType;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "posts")
@NoArgsConstructor
@Data
@ToString(exclude = {"postRatings", "tags", "comments"})
@EqualsAndHashCode(of = {"user", "title", "time"})
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "moderation_status", columnDefinition = "enum")
    @Enumerated(EnumType.STRING)
    private ModerationStatusType moderationStatus = ModerationStatusType.NEW;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User moderator;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;

    @Column(name = "time", nullable = false)
    private Date time;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "view_count", nullable = false)
    private int viewCount;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<PostVote> postRatings;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Tag2Post> tags;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<PostComment> comments;
}
