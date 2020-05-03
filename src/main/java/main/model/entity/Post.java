package main.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import main.model.ModerationStatusType;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "posts")
@NoArgsConstructor
@Data
@ToString(exclude = {"user", "moderator"})
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
}
