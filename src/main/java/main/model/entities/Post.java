package main.model.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import main.model.enums.ActivityStatus;
import main.model.enums.ModerationStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "posts")
@NoArgsConstructor
@Data
@ToString(exclude = {"postRatings", "tags", "comments"})
@EqualsAndHashCode(exclude = {"postRatings", "tags", "comments"})
public class Post implements Serializable {
    private long id;
    private ActivityStatus activityStatus;
    private ModerationStatus moderationStatus = ModerationStatus.NEW;
    private User moderator;
    private User user;
    private LocalDateTime time;
    private String title;
    private String text;
    private int viewCount;
    private Set<PostVote> postRatings;
    private Set<Tag2Post> tags;
    private Set<PostComment> comments;

    //==============================================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    @Column(name = "activity_status", nullable = false)
    @Enumerated(EnumType.STRING)
    public ActivityStatus getActivityStatus() {
        return activityStatus;
    }

    @Column(name = "moderation_status")
    @Enumerated(EnumType.STRING)
    public ModerationStatus getModerationStatus() {
        return moderationStatus;
    }

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderator_id")
    public User getModerator() {
        return moderator;
    }

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    @Column(name = "time", nullable = false)
    public LocalDateTime getTime() {
        return time;
    }

    @Column(name = "title", nullable = false)
    public String getTitle() {
        return title;
    }

    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    public String getText() {
        return text;
    }

    @Column(name = "view_count", nullable = false)
    public int getViewCount() {
        return viewCount;
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<PostVote> getPostRatings() {
        return postRatings;
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<Tag2Post> getTags() {
        return tags;
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<PostComment> getComments() {
        return comments;
    }
}
