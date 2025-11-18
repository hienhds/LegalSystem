package com.example.backend.forum.entity;


import com.example.backend.lawyer.entity.Specialization;
import com.example.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "forum_post", indexes = {
        @Index(name = "idx_forum_spec", columnList = "spec_id"),
        @Index(name = "idx_forum_user", columnList = "user_id")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForumPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spec_id", nullable = false)
    private Specialization specialization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean isLawyer;

    @Column(nullable = false)
    private boolean isHidden = false;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "update_at")
    private LocalDateTime updateAt;
}
