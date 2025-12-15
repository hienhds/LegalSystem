package com.example.backend.case_management.service;

import com.example.backend.case_management.dto.CaseResponse;
import com.example.backend.case_management.dto.CaseUpdateResponse;
import com.example.backend.case_management.dto.CreateCaseRequest;
import com.example.backend.case_management.dto.UpdateProgressRequest;
import com.example.backend.case_management.entity.Case;
import com.example.backend.case_management.entity.CaseDocument;
import com.example.backend.case_management.entity.CaseProgressUpdate;
import com.example.backend.case_management.entity.CaseStatus;
import com.example.backend.case_management.repository.CaseRepository;
import com.example.backend.common.exception.AppException;
import com.example.backend.common.exception.ErrorType;
import com.example.backend.common.service.UploadImageService;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
@Service
@RequiredArgsConstructor
@Transactional
public class CaseService {

    private final CaseRepository caseRepository;
    private final UserRepository userRepository;
    private final UploadImageService uploadService; // Inject service upload

    // --- CODE CŨ: TẠO VÀ LẤY CHI TIẾT ---
    // 1. SỬA: Luật sư tạo vụ án cho Khách hàng
    public CaseResponse createCase(Long lawyerId, CreateCaseRequest request) {
        // Lấy thông tin Luật sư (người đang đăng nhập)
        User lawyer = userRepository.findById(lawyerId)
                .orElseThrow(() -> new AppException(ErrorType.NOT_FOUND, "Lawyer not found"));
        
        // Kiểm tra chắc chắn user này có quyền luật sư (dù Controller đã check role)
        if (lawyer.getLawyer() == null) {
             throw new AppException(ErrorType.FORBIDDEN, "Tài khoản này không phải là luật sư");
        }

        // Lấy thông tin Khách hàng (từ request gửi lên)
        User client = userRepository.findById(request.getClientId())
                .orElseThrow(() -> new AppException(ErrorType.NOT_FOUND, "Client not found"));

        // Tạo vụ án mới
        Case newCase = Case.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .client(client)   // Khách hàng
                .lawyer(lawyer)   // Luật sư phụ trách
                .status(CaseStatus.IN_PROGRESS) // <--- SỬA: Trạng thái là Đang thực hiện luôn
                .build();

        Case savedCase = caseRepository.save(newCase);
        return CaseResponse.from(savedCase);
    }
    // 2. LẤY CHI TIẾT VỤ ÁN
    public CaseResponse getCaseDetail(Long caseId) {
        Case c = caseRepository.findById(caseId)
                .orElseThrow(() -> new AppException(ErrorType.NOT_FOUND, "Case not found"));
        return CaseResponse.from(c);
    }

    // --- CODE MỚI: CẬP NHẬT TIẾN ĐỘ ---
    public CaseUpdateResponse addCaseUpdate(Long caseId, Long userId, UpdateProgressRequest request) {
        Case c = caseRepository.findById(caseId)
                .orElseThrow(() -> new AppException(ErrorType.NOT_FOUND, "Không tìm thấy vụ án"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorType.NOT_FOUND, "User not found"));

        // Chỉ luật sư phụ trách mới được update
        if (!c.getLawyer().getUserId().equals(userId)) {
            throw new AppException(ErrorType.FORBIDDEN, "Bạn không phải luật sư phụ trách vụ án này");
        }

        CaseProgressUpdate update = CaseProgressUpdate.builder()
                .legalCase(c)
                .title(request.getTitle())
                .description(request.getDescription())
                .createdBy(user)
                .build();

        c.getUpdates().add(update);

        if (request.getStatus() != null) {
            c.setStatus(request.getStatus());
        }

        caseRepository.save(c);

        return CaseUpdateResponse.from(update);
    }

    // 2. SỬA: Upload tài liệu (Chỉ Luật sư mới được up)
    public String uploadCaseDocument(Long caseId, Long userId, MultipartFile file) {
        Case c = caseRepository.findById(caseId)
                .orElseThrow(() -> new AppException(ErrorType.NOT_FOUND, "Case not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorType.NOT_FOUND, "User not found"));

        // Check: Chỉ có Luật sư phụ trách vụ án này mới được upload
        if (!c.getLawyer().getUserId().equals(userId)) {
            throw new AppException(ErrorType.FORBIDDEN, "Chỉ luật sư phụ trách mới được thêm tài liệu vụ án");
        }

        String fileUrl = uploadService.uploadFile(userId, file, "case_docs");

        CaseDocument doc = CaseDocument.builder()
                .legalCase(c)
                .fileName(file.getOriginalFilename())
                .fileUrl(fileUrl)
                .uploadedBy(user)
                .build();

        c.getDocuments().add(doc);
        caseRepository.save(c);

        return fileUrl;
    }
    public Page<CaseResponse> getMyCases(Long userId, Pageable pageable) {
        // Log ID ra console để kiểm tra
        System.out.println(">>> Đang tìm vụ án cho User ID: " + userId); 

        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorType.NOT_FOUND, "User not found"));

        // Gọi hàm repository mới
        Page<Case> cases = caseRepository.findAllCasesByUserId(userId, pageable);
        
        System.out.println(">>> Tìm thấy: " + cases.getTotalElements() + " vụ án.");

        return cases.map(CaseResponse::from);
    }
}