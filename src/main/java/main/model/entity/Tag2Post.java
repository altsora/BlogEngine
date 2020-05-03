package main.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import main.model.entity.Post;
import main.model.entity.Tag;

import javax.persistence.*;

@Entity
@Table(name = "tag2post")
@NoArgsConstructor
@Data
@ToString
public class Tag2Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

//    @Column(name = "post_id", nullable = false)
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Post post;

//    @Column(name = "tag_id", nullable = false)
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Tag tag;
}
