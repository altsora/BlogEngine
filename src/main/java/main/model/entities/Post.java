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
@EqualsAndHashCode(exclude = {"postRatings", "tags", "comments"})
public class Post implements Serializable {

    private int id;
    private byte isActive;
    private ModerationStatusType moderationStatus = ModerationStatusType.NEW;
    private User moderator;
    private User user;
    private Date time;
    private String title;
    private String text;
    private int viewCount;
    private Set<PostVote> postRatings;
    private Set<Tag2Post> tags;
    private Set<PostComment> comments;

    //==============================================================================


//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
//
//    @Column(name = "is_active", nullable = false)
//    private boolean isActive;
//
//    @Column(name = "moderation_status", columnDefinition = "enum('NEW', 'ACCEPTED', 'DECLINED')")
//    @Enumerated(EnumType.STRING)
//    private ModerationStatusType moderationStatus = ModerationStatusType.NEW;
//
//    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinColumn(name = "moderator_id")
//    private User moderator;
//
//    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    @Column(name = "time", nullable = false)
//    private Date time;
//
//    @Column(name = "title", nullable = false)
//    private String title;
//
//    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
//    private String text;
//
//    @Column(name = "view_count", nullable = false)
//    private int viewCount;
//
//    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
//    private Set<PostVote> postRatings;
//
//    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
//    private Set<Tag2Post> tags;
//
//    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
//    private Set<PostComment> comments;

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

//    public byte getActive() {
//        return isActive;
//    }

    @Column(name = "moderation_status", columnDefinition = "enum('NEW', 'ACCEPTED', 'DECLINED')")
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
    public Date getTime() {
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
