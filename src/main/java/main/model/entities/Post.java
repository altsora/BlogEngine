package main.model.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import main.model.ModerationStatusType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "posts")
@NoArgsConstructor
@Data
@ToString(exclude = {"postRatings", "tags", "comments"})
@EqualsAndHashCode(of = {"user", "title", "time"})
public class Post implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "moderation_status", columnDefinition = "enum('NEW', 'ACCEPTED', 'DECLINED')")
    @Enumerated(EnumType.STRING)
    private ModerationStatusType moderationStatus = ModerationStatusType.NEW;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "moderator_id")
    private User moderator;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "time", nullable = false)
    private Date time;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "view_count", nullable = false)
    private int viewCount;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private Set<PostVote> postRatings;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private Set<Tag2Post> tags;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private Set<PostComment> comments;

    //==============================================================================


    public int getId() {
        return id;
    }

    public boolean isActive() {
        return isActive;
    }

    public ModerationStatusType getModerationStatus() {
        return moderationStatus;
    }

    public Date getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public int getViewCount() {
        return viewCount;
    }

    @JsonBackReference
    public User getModerator() {
        return moderator;
    }

    @JsonBackReference
    public User getUser() {
        return user;
    }

    @JsonManagedReference
    public Set<PostVote> getPostRatings() {
        return postRatings;
    }

    @JsonManagedReference
    public Set<Tag2Post> getTags() {
        return tags;
    }

    @JsonManagedReference
    public Set<PostComment> getComments() {
        return comments;
    }
}
