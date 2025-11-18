package com.example.backend.lawyer.dto.request;


import com.example.backend.lawyer.entity.VerificationStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterLawyerRequest {
    // loc theo trang thai
    private VerificationStatus status;
    private Long barAssociationId;
    private String keyword;
    private String sortBy = "createdAt";
    private String sortDir = "desc";
}
