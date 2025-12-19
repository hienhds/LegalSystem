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
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Transactional
public class CaseService {

    private final CaseRepository caseRepository;
    private final UserRepository userRepository;
    private final UploadImageService uploadService;

    // 1. TẠO VỤ ÁN
    public CaseResponse createCase(Long lawyerId, CreateCaseRequest request) {
        User lawyer = userRepository.findById(lawyerId)
                .orElseThrow(() -> new AppException(ErrorType.NOT_FOUND, "Lawyer not found"));
        
        if (lawyer.getLawyer() == null) {
             throw new AppException(ErrorType.FORBIDDEN, "Tài khoản này không phải là luật sư");
        }

        User client = userRepository.findById(request.getClientId())
                .orElseThrow(() -> new AppException(ErrorType.NOT_FOUND, "Client not found"));

        Case newCase = Case.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .client(client)
                .lawyer(lawyer)
                .status(CaseStatus.IN_PROGRESS)
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

    // 3. CẬP NHẬT TIẾN ĐỘ
    public CaseUpdateResponse addCaseUpdate(Long caseId, Long userId, UpdateProgressRequest request) {
        Case c = caseRepository.findById(caseId)
                .orElseThrow(() -> new AppException(ErrorType.NOT_FOUND, "Không tìm thấy vụ án"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorType.NOT_FOUND, "User not found"));

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

    // 4. UPLOAD TÀI LIỆU
    public String uploadCaseDocument(Long caseId, Long userId, MultipartFile file) {
        Case c = caseRepository.findById(caseId)
                .orElseThrow(() -> new AppException(ErrorType.NOT_FOUND, "Case not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorType.NOT_FOUND, "User not found"));

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

    // 5. LẤY DANH SÁCH VỤ ÁN CỦA TÔI (CÓ TÌM KIẾM)
    public Page<CaseResponse> getMyCases(Long userId, String keyword, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorType.NOT_FOUND, "User not found"));

        Page<Case> casesPage;
        String role = user.getRoleName(); 
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();

        // Nếu user là Luật sư (LAWYER)
        if ("LAWYER".equals(role)) {
            if (hasKeyword) {
                casesPage = caseRepository.searchCasesForLawyer(userId, keyword.trim(), pageable);
            } else {
                casesPage = caseRepository.findByLawyer_UserId(userId, pageable);
            }
        } 
        // Nếu user là Người dân (USER/CITIZEN) - Hoặc Admin
        else {
            if (hasKeyword) {
                casesPage = caseRepository.searchCasesForCitizen(userId, keyword.trim(), pageable);
            } else {
                casesPage = caseRepository.findByClient_UserId(userId, pageable);
            }
        }

        return casesPage.map(CaseResponse::from);
    }

    // 6. DOWNLOAD TÀI LIỆU (MỚI - Dùng để fix lỗi xem file)
    public Resource downloadCaseDocument(Long caseId, Long docId, Long userId) {
        // 1. Tìm vụ án
        Case c = caseRepository.findById(caseId)
            .orElseThrow(() -> new AppException(ErrorType.NOT_FOUND, "Không tìm thấy vụ án"));

        // 2. Tìm tài liệu trong vụ án (duyệt list để tìm đúng docId)
        CaseDocument doc = c.getDocuments().stream()
                .filter(d -> d.getDocId().equals(docId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorType.NOT_FOUND, "Không tìm thấy tài liệu"));

        // 3. CHECK QUYỀN: Chỉ Luật sư phụ trách hoặc Khách hàng của vụ án mới được xem
        boolean isLawyer = c.getLawyer().getUserId().equals(userId);
        boolean isClient = c.getClient().getUserId().equals(userId);

        if (!isLawyer && !isClient) {
            throw new AppException(ErrorType.FORBIDDEN, "Bạn không có quyền truy cập tài liệu này");
        }

        // 4. Lấy file từ ổ cứng
        try {
            // Lưu ý: doc.getFileUrl() có thể là "uploads/case_docs/abc.pdf" hoặc "/case_docs/abc.pdf"
            // Cần xử lý để ra đường dẫn tuyệt đối chính xác
            String storedPath = doc.getFileUrl();
            // Xóa prefix /uploads/ hoặc uploads/ nếu có để tránh trùng lặp
            if (storedPath.startsWith("/uploads/")) {
                storedPath = storedPath.substring(9);
            } else if (storedPath.startsWith("uploads/")) {
                storedPath = storedPath.substring(8);
            }

            Path filePath = Paths.get("uploads").resolve(storedPath).normalize();
            
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new AppException(ErrorType.NOT_FOUND, "File không tồn tại trên hệ thống");
            }
        } catch (MalformedURLException e) {
            throw new AppException(ErrorType.INTERNAL_ERROR, "Lỗi đường dẫn file");
        }
    }
}