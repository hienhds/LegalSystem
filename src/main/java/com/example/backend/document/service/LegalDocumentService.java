package com.example.backend.document.service;

import com.example.backend.document.entity.LegalDocument;
import com.example.backend.document.entity.LegalDocument.DocumentStatus;
import com.example.backend.document.repository.LegalDocumentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LegalDocumentService {

    private final LegalDocumentRepository legalDocumentRepository;

    /**
     * Get document by ID and increment view count
     */
    @Transactional
    public LegalDocument getDocumentById(Long documentId) {
        log.info("Getting document with ID: {}", documentId);
        
        LegalDocument document = legalDocumentRepository.findById(documentId).orElse(null);
        
        if (document == null) {
            log.warn("Không tìm thấy văn bản pháp luật với ID: {}", documentId);
            return null;
        }
        
        // Increment view count
        legalDocumentRepository.incrementViewCount(documentId);
        document.setViewCount(document.getViewCount() + 1); // Update in memory for response
        
        log.info("Document found: {} (Views: {})", document.getTitle(), document.getViewCount());
        return document;
    }

    /**
     * Search documents by title keyword
     */
    public Page<LegalDocument> searchByTitle(String keyword, int page, int size) {
        log.info("Searching documents by title keyword: '{}'", keyword);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<LegalDocument> documents = legalDocumentRepository.findByTitleContaining(
            keyword, DocumentStatus.ACTIVE, pageable);
        
        log.info("Found {} documents matching title: '{}'", documents.getTotalElements(), keyword);
        return documents;
    }

    /**
     * Get documents by category
     */
    public Page<LegalDocument> getDocumentsByCategory(String category, int page, int size) {
        log.info("Getting documents by category: {}", category);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<LegalDocument> documents = legalDocumentRepository.findByCategory(
            category, DocumentStatus.ACTIVE, pageable);
        
        log.info("Found {} documents in category: {}", documents.getTotalElements(), category);
        return documents;
    }

    /**
     * Advanced search with title and category filters
     */
    public Page<LegalDocument> advancedSearch(String keyword, String category, int page, int size) {
        log.info("Advanced search - keyword: '{}', category: '{}'", keyword, category);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<LegalDocument> documents;
        
        if (keyword != null && !keyword.trim().isEmpty() && category != null && !category.trim().isEmpty()) {
            // Both keyword and category
            documents = legalDocumentRepository.findByTitleContainingAndCategory(
                keyword, category, DocumentStatus.ACTIVE, pageable);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            // Only keyword
            documents = legalDocumentRepository.searchByKeyword(
                keyword, DocumentStatus.ACTIVE, pageable);
        } else if (category != null && !category.trim().isEmpty()) {
            // Only category
            documents = legalDocumentRepository.findByCategory(
                category, DocumentStatus.ACTIVE, pageable);
        } else {
            // No filters - get all active documents
            documents = legalDocumentRepository.findByStatus(DocumentStatus.ACTIVE, pageable);
        }
        
        log.info("Advanced search found {} documents", documents.getTotalElements());
        return documents;
    }

    /**
     * Get trending/popular documents
     */
    public Page<LegalDocument> getTrendingDocuments(int page, int size) {
        log.info("Getting trending documents");
        
        Pageable pageable = PageRequest.of(page, size);
        Page<LegalDocument> documents = legalDocumentRepository.findTrendingDocuments(
            DocumentStatus.ACTIVE, pageable);
        
        log.info("Found {} trending documents", documents.getTotalElements());
        return documents;
    }

    /**
     * Get all available categories
     */
    public List<String> getAllCategories() {
        log.info("Getting all available categories");
        
        List<String> categories = legalDocumentRepository.findAllCategories(DocumentStatus.ACTIVE);
        
        log.info("Found {} categories", categories.size());
        return categories;
    }

    /**
     * Get documents count by category (for statistics)
     */
    public List<Object[]> getDocumentCountsByCategory() {
        log.info("Getting document counts by category");
        
        List<Object[]> counts = legalDocumentRepository.countDocumentsByCategory(DocumentStatus.ACTIVE);
        
        log.info("Retrieved document counts for {} categories", counts.size());
        return counts;
    }

    /**
     * Get all active documents with pagination
     */
    public Page<LegalDocument> getAllDocuments(int page, int size, String sortBy, String sortDirection) {
        log.info("Getting all documents - page: {}, size: {}, sortBy: {}, direction: {}", 
                 page, size, sortBy, sortDirection);
        
        // Default sorting
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        
        // Validate sort field
        String sortField;
        switch (sortBy.toLowerCase()) {
            case "title":
                sortField = "title";
                break;
            case "category":
                sortField = "category";
                break;
            case "views":
            case "viewcount":
                sortField = "viewCount";
                break;
            case "created":
            case "createdat":
                sortField = "createdAt";
                break;
            default:
                sortField = "createdAt"; // Default sort
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<LegalDocument> documents = legalDocumentRepository.findByStatus(
            DocumentStatus.ACTIVE, pageable);
        
        log.info("Retrieved {} total documents", documents.getTotalElements());
        return documents;
    }

    /**
     * Check if document exists
     */
    public boolean documentExists(Long documentId) {
        return legalDocumentRepository.existsById(documentId);
    }

    /**
     * Create new legal document (for Admin and Lawyer)
     */
    @Transactional
    public LegalDocument createDocument(String title, String category, String fileUrl) {
        log.info("Creating new legal document: title='{}', category='{}'", title, category);
        
        LegalDocument document = LegalDocument.builder()
                .title(title)
                .category(category)
                .fileUrl(fileUrl)
                .status(DocumentStatus.ACTIVE)
                .viewCount(0)
                .build();
        
        LegalDocument savedDocument = legalDocumentRepository.save(document);
        log.info("Legal document created with ID: {}", savedDocument.getDocumentId());
        
        return savedDocument;
    }

    /**
     * General search (searches in both title and category)
     */
    public Page<LegalDocument> generalSearch(String keyword, int page, int size) {
        log.info("General search with keyword: '{}'", keyword);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            // Return all documents if no keyword
            return getAllDocuments(page, size, "createdAt", "desc");
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<LegalDocument> documents = legalDocumentRepository.searchByKeyword(
            keyword.trim(), DocumentStatus.ACTIVE, pageable);
        
        log.info("General search found {} documents for keyword: '{}'", 
                 documents.getTotalElements(), keyword);
        return documents;
    }
}