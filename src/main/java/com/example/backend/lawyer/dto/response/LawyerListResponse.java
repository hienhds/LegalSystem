package com.example.backend.lawyer.dto.response;


import com.example.backend.lawyer.entity.VerificationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class LawyerListResponse {

    private Long lawyerId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private String barAssociationName;
    private String barLicenseId;
    private String certificateUrl;
    private VerificationStatus verificationStatus;
}
