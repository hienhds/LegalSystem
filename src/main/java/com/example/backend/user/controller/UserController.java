package com.example.backend.user.controller;

import com.example.backend.admin.dto.UserManagementResponse;
import com.example.backend.common.dto.ApiResponse;
import com.example.backend.common.security.CustomUserDetails;
import com.example.backend.common.service.UploadImageService;
import com.example.backend.lawyer.dto.request.LawyerRequest;
import com.example.backend.lawyer.dto.response.LawyerResponse;
import com.example.backend.lawyer.service.LawyerService;
import com.example.backend.user.dto.UserProfileUpdateRequest;
import com.example.backend.user.dto.UserResponse;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import com.example.backend.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UploadImageService uploadImageService;
    private final LawyerService lawyerService;
    private final UserRepository userRepository;

    // 🔥 FIX QUAN TRỌNG: Thêm :[0-9]+ để chỉ nhận ID là số. 
    // Giúp đường dẫn /search không bị lọt vào đây nữa.
    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Object>> getCurrentUserProfile(
            HttpServletRequest httpRequest,
            @AuthenticationPrincipal CustomUserDetails user) {

        Long userId = user.getUser().getUserId();
        Object profileData = userService.getUserProfile(userId);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("Lấy thông tin hồ sơ thành công")
                .data(profileData)
                .timestamp(Instant.now())
                .path(httpRequest.getRequestURI())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @RequestBody UserProfileUpdateRequest request,
            HttpServletRequest httpRequest,
            @AuthenticationPrincipal CustomUserDetails user) {

        Long userId = user.getUser().getUserId();
        
        UserProfileUpdateRequest limitedRequest = new UserProfileUpdateRequest();
        limitedRequest.setAddress(request.getAddress());
        limitedRequest.setFullName(request.getFullName());
        limitedRequest.setPhoneNumber(request.getPhoneNumber());
        
        UserResponse userResponse = userService.updateProfile(userId, limitedRequest);

        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("Cập nhật hồ sơ thành công")
                .data(userResponse)
                .timestamp(Instant.now())
                .path(httpRequest.getRequestURI())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/avatar", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<String>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {

        Long userId = user.getUser().getUserId();
        String avatarUrl = userService.updateAvatar(userId, file);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("Upload avatar thành công")
                .data(avatarUrl)
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/register-lawyer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<LawyerResponse>> registerAsLawyer(
            @RequestParam("data") String data,
            @RequestParam("certificate") MultipartFile file,
            HttpServletRequest servletRequest,
            @AuthenticationPrincipal CustomUserDetails user
    ) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        LawyerRequest request = mapper.readValue(data, LawyerRequest.class);

        Long userId = user.getUser().getUserId();
        String certificateUrl = uploadImageService.uploadImage(userId, file, "certificates");
        LawyerResponse lawyerResponse = lawyerService.requestUpgrade(request, userId, certificateUrl);

        ApiResponse<LawyerResponse> response = ApiResponse.<LawyerResponse>builder()
                .success(true)
                .status(HttpStatus.CREATED.value())
                .message("Đăng ký trở thành luật sư thành công, vui lòng chờ phê duyệt")
                .data(lawyerResponse)
                .path(servletRequest.getRequestURI())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 🔥 API TÌM KIẾM: Bây giờ đã an toàn và sử dụng searchClients để lọc đúng người
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('LAWYER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserManagementResponse>>> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fullName").ascending());
        
        String searchTerm = keyword != null ? keyword : "";
        
        // Gọi hàm searchClients đã thêm bên Repository
        Page<User> usersPage = userRepository.searchClients(searchTerm, pageable);

        Page<UserManagementResponse> responsePage = usersPage.map(u -> UserManagementResponse.builder()
                .userId(u.getUserId())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .phoneNumber(u.getPhoneNumber())
                .avatarUrl(u.getAvatarUrl())
                .role(u.getRoleName())
                .build());

        ApiResponse<Page<UserManagementResponse>> response = ApiResponse.<Page<UserManagementResponse>>builder()
                .success(true)
                .message("Tìm kiếm thành công")
                .data(responsePage)
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.ok(response);
    }
}