package com.pjsofttech.library.service;

import com.pjsofttech.library.dto.response.DashboardStatsResponse;
import com.pjsofttech.library.model.*;
import com.pjsofttech.library.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DashboardService {

//    private final BookRepository bookRepository;
//    private final BookCopyRepository bookCopyRepository;
//    private final MemberRepository memberRepository;
//    private final LoanRepository loanRepository;
//    private final ReservationRepository reservationRepository;
//    private final FineRepository fineRepository;
//
//    private BigDecimal calculateTotalFinePaid(){
//        List<Fine> fines = fineRepository.findAll()
//                .stream().filter(f->f.getStatus().equals(FineStatus.PAID))
//                .toList();
//        return fines.stream()
//                .map(Fine::getTotalAmount)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }
//
//    private BigDecimal calculateTotalFinePending(){
//        return fineRepository.findAll()
//                .stream()
//                .filter(f->f.getStatus().equals(FineStatus.PENDING))
//                .map(Fine::getTotalAmount)
//                .reduce(BigDecimal.ZERO,BigDecimal::add);
//    }
//
//    @Transactional(readOnly = true)
//    public DashboardStatsResponse getStats(String branchCode) {
//        long totalBooks = bookRepository.count();
//        long totalCopies = bookCopyRepository.count();
//        long availableCopies = bookCopyRepository.countByBookIdAndStatus(0L, CopyStatus.AVAILABLE); // rough placeholder
//        // Better: sum availableCopies across all books
//        long totalAvailable = bookRepository.findAll().stream()
//                .mapToLong(b -> b.getAvailableCopies()).sum();
//
//        long totalMembers = memberRepository.count();
//        long activeMembers = memberRepository.findByStatus(MemberStatus.ACTIVE,
//                org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
//
//        long activeLoans = loanRepository.findByStatus(LoanStatus.ACTIVE,
//                org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
//
//        long overdueLoans = loanRepository.findOverdueLoansPage(LocalDate.now(),
//                org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
//
//        long pendingReservations = reservationRepository.findByStatus(ReservationStatus.PENDING,
//                org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
//
//        long pendingFines = fineRepository.findByStatus(FineStatus.PENDING,
//                org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
//
//        long paidFines = fineRepository.findByStatus(FineStatus.PAID,
//                org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
//
//
//
//        BigDecimal totalFineAmount = calculateTotalFinePending();
//
//
//
//        BigDecimal totalFinePaidAmount = calculateTotalFinePaid();
//
//
//
//
//
//        return DashboardStatsResponse.builder()
//                .totalBooks(totalBooks)
//                .totalBookCopies(totalCopies)
//                .availableCopies(totalAvailable)
//                .totalMembers(totalMembers)
//                .activeMembers(activeMembers)
//                .activeLoans(activeLoans)
//                .overdueLoans(overdueLoans)
//                .pendingReservations(pendingReservations)
//                .pendingFines(pendingFines)
//                .paidFines(paidFines)
//                .totalPendingFineAmount(totalFineAmount)
//                .totalPaidFineAmount(totalFinePaidAmount)
//                .build();
//    }

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;
    private final FineRepository fineRepository;

    @Transactional(readOnly = true)
    public DashboardStatsResponse getStats(String branchCode) {

        long totalBooks =
                bookRepository.countByBranchCode(branchCode);

        long totalCopies =
                bookCopyRepository.countByBranchCode(branchCode);

        long availableCopies =
                bookCopyRepository.countByBranchCodeAndStatus(
                        branchCode,
                        CopyStatus.AVAILABLE
                );

        long totalMembers =
                memberRepository.countByBranchCode(branchCode);

        long activeMembers =
                memberRepository.countByBranchCodeAndStatus(
                        branchCode,
                        MemberStatus.ACTIVE
                );

        long activeLoans =
                loanRepository.countByBranchCodeAndStatus(
                        branchCode,
                        LoanStatus.ACTIVE
                );

        long overdueLoans =
                loanRepository.countByBranchCodeAndStatus(
                        branchCode,
                        LoanStatus.OVERDUE
                );

        long pendingReservations =
                reservationRepository.countByBranchCodeAndStatus(
                        branchCode,
                        ReservationStatus.PENDING
                );

        long pendingFines =
                fineRepository.countByBranchCodeAndStatus(
                        branchCode,
                        FineStatus.PENDING
                );

        long paidFines =
                fineRepository.countByBranchCodeAndStatus(
                        branchCode,
                        FineStatus.PAID
                );

        BigDecimal totalPendingFineAmount =
                fineRepository.sumTotalAmountByBranchCodeAndStatus(
                        branchCode,
                        FineStatus.PENDING
                );

        BigDecimal totalPaidFineAmount =
                fineRepository.sumTotalAmountByBranchCodeAndStatus(
                        branchCode,
                        FineStatus.PAID
                );

        return DashboardStatsResponse.builder()
                .totalBooks(totalBooks)
                .totalBookCopies(totalCopies)
                .availableCopies(availableCopies)
                .totalMembers(totalMembers)
                .activeMembers(activeMembers)
                .activeLoans(activeLoans)
                .overdueLoans(overdueLoans)
                .pendingReservations(pendingReservations)
                .pendingFines(pendingFines)
                .paidFines(paidFines)
                .totalPendingFineAmount(
                        totalPendingFineAmount != null
                                ? totalPendingFineAmount
                                : java.math.BigDecimal.ZERO
                )
                .totalPaidFineAmount(
                        totalPaidFineAmount != null
                                ? totalPaidFineAmount
                                : java.math.BigDecimal.ZERO
                )
                .build();
    }
}