package com.pjsofttech.library.repository;

import com.pjsofttech.library.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
    Page<Category> findAllByBranchCode(String branchCode, Pageable pageable);
    List<Category> findAllByBranchCode(String branchCode);

    long countByBranchCode(String branchCode);
}