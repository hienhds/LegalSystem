package com.example.backend.case_management.dto;

import com.example.backend.case_management.entity.Case;
import com.example.backend.case_management.entity.CaseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseResponse {
    private Long caseId;
    private String title;
    private String description;
    private CaseStatus status;
    private LocalDateTime createdAt;
    
    // Chỉ lấy ID và Tên
    private Long clientId;
    private String clientName;
    private Long lawyerId;
    private String lawyerName;
    
    private List<CaseDocumentResponse> documents;
    private List<CaseUpdateResponse> updates; 

    public static CaseResponse from(Case c) {
        return CaseResponse.builder()
                .caseId(c.getCaseId())
                .title(c.getTitle())
                .description(c.getDescription())
                .status(c.getStatus())
                .createdAt(c.getCreatedAt())
                
                // Lấy thông tin user an toàn
                .clientId(c.getClient() != null ? c.getClient().getUserId() : null)
                .clientName(c.getClient() != null ? c.getClient().getFullName() : "Unknown")
                
                .lawyerId(c.getLawyer() != null ? c.getLawyer().getUserId() : null)
                .lawyerName(c.getLawyer() != null ? c.getLawyer().getFullName() : "Unknown")
                
                // Map documents (nếu null thì trả về list rỗng)
                .documents(c.getDocuments() != null ? 
                    c.getDocuments().stream().map(CaseDocumentResponse::from).collect(Collectors.toList()) 
                    : Collections.emptyList())
                
                // Map updates (nếu null thì trả về list rỗng)
                .updates(c.getUpdates() != null ?
                    c.getUpdates().stream().map(CaseUpdateResponse::from).collect(Collectors.toList())
                    : Collections.emptyList())
                    
                .build();
    }
}