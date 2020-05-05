package main.model.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "tag2post")
@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode(of = {"post", "tag"})
public class Tag2Post implements Serializable {

    private int id;
    private Post post;
    private Tag tag;

    //==============================================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    public Post getPost() {
        return post;
    }

    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tag_id")
    public Tag getTag() {
        return tag;
    }

//        @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
//
//    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "post_id")
//    private Post post;
//
//    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "tag_id")
//    private Tag tag;
//
////    ==============================================================================
//
//    @JsonBackReference
//    public Post getPost() {
//        return post;
//    }
//
//    @JsonBackReference
//    public Tag getTag() {
//        return tag;
//    }
}
