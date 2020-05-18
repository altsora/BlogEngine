package main.model.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import main.model.entities.enums.ModerationStatusType;

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

    private int id;
    private byte isActive;
    private ModerationStatusType moderationStatus = ModerationStatusType.NEW;
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
    public int getId() {
        return id;
    }

    @Column(name = "is_active", nullable = false)
    public byte getIsActive() {
        return isActive;
    }

    @Column(name = "moderation_status")
    @Enumerated(EnumType.STRING)
    public ModerationStatusType getModerationStatus() {
        return moderationStatus;
    }

    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "moderator_id")
    public User getModerator() {
        return moderator;
    }

    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
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
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    public Set<PostVote> getPostRatings() {
        return postRatings;
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    public Set<Tag2Post> getTags() {
        return tags;
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    public Set<PostComment> getComments() {
        return comments;
    }

}
