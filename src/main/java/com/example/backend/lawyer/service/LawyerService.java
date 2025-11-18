package com.example.backend.lawyer.service;

import com.example.backend.common.exception.AppException;
import com.example.backend.common.exception.ErrorType;
import com.example.backend.common.service.EmailService;
import com.example.backend.lawyer.dto.request.FilterLawyerRequest;
import com.example.backend.lawyer.dto.request.LawyerRequest;
import com.example.backend.lawyer.dto.response.LawyerDetailResponse;
import com.example.backend.lawyer.dto.response.LawyerListResponse;
import com.example.backend.lawyer.dto.response.LawyerResponse;
import com.example.backend.lawyer.entity.*;
import com.example.backend.lawyer.repository.BarAssociationRepository;
import com.example.backend.lawyer.repository.LawyerRepository;
import com.example.backend.lawyer.repository.LawyerSpecializationRepository;
import com.example.backend.lawyer.repository.SpecializationRepository;
import com.example.backend.lawyer.spec.LawyerSpec;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class LawyerService {

    private final LawyerRepository lawyerRepository;
    private final BarAssociationRepository barAssociationRepository;
    private final SpecializationRepository specializationRepository;
    private final UserRepository userRepository;
    private final LawyerSpecializationRepository lawyerSpecializationRepository;
    private final EmailService emailService;




    public LawyerResponse requestUpgrade(LawyerRequest request, Long userId, String certificateUrl){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorType.NOT_FOUND, "User not found"));

        if(user.getLawyer() != null){
            throw new AppException(ErrorType.CONFLICT, "ban da gui yeu cau hoac da la luat su");

        }

        BarAssociation barAssociation = barAssociationRepository.findById(request.getBarAssociationId())
                .orElseThrow(() -> new AppException(ErrorType.NOT_FOUND, "lien doan luat su khong ton tai"));


        if(user.getAvatarUrl() == null || user.getAvatarUrl().isBlank()){
            throw new AppException(ErrorType.BAD_REQUEST, "update avatar before give request");

        }

        if(user.getAddress() == null || user.getAddress().isBlank()){
            throw  new AppException(ErrorType.BAD_REQUEST, "update address before give request");
        }

        Lawyer lawyer = Lawyer.builder()
                .user(user)
                .barLicenseId(request.getBarLicenseId())
                .bio(request.getBio())
                .certificateImageUrl(certificateUrl)
                .officeAddress(request.getOfficeAddress())
                .yearsOfExp(request.getYearsOfExp())
                .barAssociation(barAssociation)
                .verificationStatus(VerificationStatus.PENDING)
                .build();

        Lawyer saved = lawyerRepository.save(lawyer);

        // Lưu Specializations
        request.getSpecializationIds().forEach(id -> {
            Specialization sp = specializationRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorType.NOT_FOUND, "Specialization không tồn tại: " + id));

            LawyerSpecialization ls = new LawyerSpecialization();
            ls.setLawyer(saved);
            ls.setSpecialization(sp);


            lawyerSpecializationRepository.save(ls);
        });

        return LawyerResponse.builder()
                .lawyerId(saved.getLawyerId())
                .fullName(user.getFullName())
                .barLicenseId(saved.getBarLicenseId())
                .bio(saved.getBio())
                .certificateImageUrl(saved.getCertificateImageUrl())
                .officeAddress(saved.getOfficeAddress())
                .yearsOfExp(saved.getYearsOfExp())
                .barAssociationName(saved.getBarAssociation().getAssociationName())
                .verificationStatus(saved.getVerificationStatus().name())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    public Page<LawyerListResponse> getAllLawyers(FilterLawyerRequest request, int page, int size){
        Sort sort = request.getSortDir().equalsIgnoreCase("asc")
                ? Sort.by(request.getSortBy()).ascending()
                : Sort.by(request.getSortBy()).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        var spec = LawyerSpec.filter(request.getStatus(), request.getBarAssociationId(), request.getKeyword());

        Page<Lawyer> lawyerPage = lawyerRepository.findAll(spec, pageable);

        return lawyerPage.map(lawyer ->
                LawyerListResponse.builder()
                        .lawyerId(lawyer.getLawyerId())
                        .fullName(lawyer.getUser().getFullName())
                        .email(lawyer.getUser().getEmail())
                        .phoneNumber(lawyer.getUser().getPhoneNumber())
                        .avatarUrl(lawyer.getUser().getAvatarUrl())
                        .barLicenseId(lawyer.getBarLicenseId())
                        .barAssociationName(lawyer.getBarAssociation().getAssociationName())
                        .verificationStatus(lawyer.getVerificationStatus())
                        .certificateUrl(lawyer.getCertificateImageUrl())
                        .build()
        );

    }

    // ============= GET DETAIL =============
    public LawyerDetailResponse getDetail(Long id) {
        Lawyer lawyer = lawyerRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorType.NOT_FOUND, "Lawyer not found"));

        return LawyerDetailResponse.builder()
                .lawyerId(lawyer.getLawyerId())
                .fullName(lawyer.getUser().getFullName())
                .email(lawyer.getUser().getEmail())
                .phoneNumber(lawyer.getUser().getPhoneNumber())
                .avatarUrl(lawyer.getUser().getAvatarUrl())

                .barLicenseId(lawyer.getBarLicenseId())
                .bio(lawyer.getBio())
                .certificateImageUrl(lawyer.getCertificateImageUrl())
                .officeAddress(lawyer.getOfficeAddress())
                .yearsOfExp(lawyer.getYearsOfExp())

                .barAssociationName(lawyer.getBarAssociation().getAssociationName())
                .verificationStatus(lawyer.getVerificationStatus())

                .specializationNames(
                        lawyer.getSpecializations().stream()
                                .map(ls -> ls.getSpecialization().getSpecName())
                                .toList()
                )

                .createdAt(lawyer.getCreatedAt())
                .updatedAt(lawyer.getUpdatedAt())
                .build();
    }

    public String updateStatus(Long id, VerificationStatus newStatus) {

        Lawyer lawyer = lawyerRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorType.NOT_FOUND, "Lawyer not found"));

        lawyer.setVerificationStatus(newStatus);
        if (newStatus == VerificationStatus.APPROVED) {
            lawyer.setVerifiedAt(LocalDateTime.now());
        }

        lawyerRepository.save(lawyer);

        // gửi email
        String email = lawyer.getUser().getEmail();
        String subject = "Trạng thái yêu cầu nâng cấp luật sư";
        String body = "Xin chào " + lawyer.getUser().getFullName() + "\n\n"
                + "Yêu cầu nâng cấp luật sư của bạn đã được cập nhật thành: "
                + newStatus + ".\n\nCảm ơn bạn.";

        emailService.sendSimpleEmail(email, subject, body);

        return "Status updated to " + newStatus;
    }

}
