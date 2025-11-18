package com.example.backend.lawyer.spec;

import com.example.backend.lawyer.entity.Lawyer;
import com.example.backend.lawyer.entity.Specialization;
import com.example.backend.lawyer.entity.VerificationStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class LawyerSpec {
    public static Specification<Lawyer> filter(
            VerificationStatus verificationStatus,
            Long barAssociationId,
            String keyword
    ){
        return ((root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if(verificationStatus != null){
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("verificationStatus"), verificationStatus));

            }

            if(barAssociationId != null){
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("barAssociation").get("id"), barAssociationId));
            }

            if(keyword != null && !keyword.isEmpty() && !keyword.isBlank()){
                Predicate p1 = criteriaBuilder.like(root.get("barLicenseId"), "%" + keyword + "%");
                Predicate p2 = criteriaBuilder.like(root.get("user").get("fullName"), "%" + keyword + "%");

                predicate = criteriaBuilder.and(predicate, criteriaBuilder.or(p1, p2));
            }

            return predicate;
        });
    }
}
