package com.example.backend.case_management.dto;

import lombok.Data;

@Data
public class CreateCaseRequest {
    private String title;
    private String description;
    
    // SỬA: Thay lawyerId thành clientId (ID của khách hàng)
    private Long clientId; 
    
    // (Optional) Có thể thêm budget hoặc các thông tin khác
}