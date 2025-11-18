package com.example.backend.forum.entity;

import com.example.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "forum_hidden_log")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ForumHiddenLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private ForumPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;


    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
