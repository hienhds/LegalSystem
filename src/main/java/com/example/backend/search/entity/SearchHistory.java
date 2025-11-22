package com.example.backend.search.entity;

import com.example.backend.user.entity.User;
import com.example.backend.lawyer.entity.Lawyer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_histories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long searchId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // Null nếu là anonymous search
    
    @Column(nullable = false)
    private String searchQuery;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SearchType searchType;
    
    @Column(columnDefinition = "JSON")
    private String searchFilters; // JSON string chứa filters
    
    private Integer resultsCount;
    
    // Click tracking
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clicked_lawyer_id")
    private Lawyer clickedLawyer;
    
    private LocalDateTime clickedAt;
    
    @CreationTimestamp
    private LocalDateTime searchTimestamp;
    
    private String userAgent;
    private String ipAddress;
    
    public enum SearchType {
        LAWYER_SEARCH,
        QUICK_SEARCH,
        FILTER_SEARCH,
        LOCATION_SEARCH
    }
}