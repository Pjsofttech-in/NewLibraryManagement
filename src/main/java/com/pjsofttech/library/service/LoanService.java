package com.pjsofttech.library.service;

import com.pjsofttech.library.dto.request.LoanRequest;
import com.pjsofttech.library.dto.response.LoanResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoanService {
    LoanResponse issueBook(LoanRequest request, String issuedByEmail,String branchCode);
    LoanResponse returnBook(Long loanId);
    LoanResponse renewLoan(Long loanId);
    LoanResponse getById(Long id);
    Page<LoanResponse> getAll(String branchCode,Pageable pageable);
    Page<LoanResponse> getLoansByMember(Long memberId, Pageable pageable);
    Page<LoanResponse> getOverdueLoans(String branchCode,Pageable pageable);
    void markOverdueLoans();
}