package com.pjsofttech.library.config;

import com.pjsofttech.library.model.*;
import com.pjsofttech.library.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Runs once at startup and populates the database with sample data
 * if the tables are empty. Safe to re-run (idempotent).
 *
 * Default credentials:
 *   ADMIN    → admin@library.com     / Admin@123
 *   LIBRARIAN→ librarian@library.com / Lib@12345
 *   MEMBER   → member@library.com    / Member@123
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("DataSeeder: database already seeded, skipping.");
            return;
        }
        log.info("DataSeeder: seeding database...");

        // ── Users ──────────────────────────────────────────────────────────────
        User admin = userRepository.save(User.builder()
                .name("Admin User")
                .email("admin@library.com")
                .password(passwordEncoder.encode("Admin@123"))
                .dateOfBirth(LocalDate.of(1985, 1, 15))
                .role(Role.ADMIN)
                .active(true)
                .build());

        User librarian = userRepository.save(User.builder()
                .name("Jane Librarian")
                .email("librarian@library.com")
                .password(passwordEncoder.encode("Lib@12345"))
                .dateOfBirth(LocalDate.of(1990, 6, 20))
                .role(Role.LIBRARIAN)
                .active(true)
                .build());

        User memberUser = userRepository.save(User.builder()
                .name("Rahul Sharma")
                .email("member@library.com")
                .password(passwordEncoder.encode("Member@123"))
                .dateOfBirth(LocalDate.of(2000, 3, 10))
                .role(Role.MEMBER)
                .active(true)
                .build());

        User memberUser2 = userRepository.save(User.builder()
                .name("Priya Patel")
                .email("priya@library.com")
                .password(passwordEncoder.encode("Member@123"))
                .dateOfBirth(LocalDate.of(2001, 8, 25))
                .role(Role.MEMBER)
                .active(true)
                .build());

        // ── Members ────────────────────────────────────────────────────────────
        memberRepository.save(Member.builder()
                .user(memberUser)
                .membershipNumber("LIB-RAHUL001")
                .phone("9876543210")
                .address("123, MG Road, Nanded, Maharashtra")
                .membershipDate(LocalDate.now())
                .membershipExpiryDate(LocalDate.now().plusYears(1))
                .status(MemberStatus.ACTIVE)
                .build());

        memberRepository.save(Member.builder()
                .user(memberUser2)
                .membershipNumber("LIB-PRIYA002")
                .phone("9123456789")
                .address("45, Station Road, Pune, Maharashtra")
                .membershipDate(LocalDate.now())
                .membershipExpiryDate(LocalDate.now().plusYears(1))
                .status(MemberStatus.ACTIVE)
                .build());

        // ── Categories ─────────────────────────────────────────────────────────
        Category programming = categoryRepository.save(Category.builder()
                .name("Programming")
                .description("Software development and programming books")
                .build());

        Category selfHelp = categoryRepository.save(Category.builder()
                .name("Self Help")
                .description("Personal development and self improvement")
                .build());

        Category science = categoryRepository.save(Category.builder()
                .name("Science")
                .description("Scientific literature and research")
                .build());

        Category fiction = categoryRepository.save(Category.builder()
                .name("Fiction")
                .description("Novels and fictional literature")
                .build());

        // ── Authors ────────────────────────────────────────────────────────────
        Author martinFowler = authorRepository.save(Author.builder()
                .name("Martin Fowler")
                .biography("Software engineer and author known for his work on object-oriented design.")
                .nationality("British")
                .build());

        Author robertMartin = authorRepository.save(Author.builder()
                .name("Robert C. Martin")
                .biography("Author of Clean Code and Agile Software Development.")
                .nationality("American")
                .build());

        Author joshBloch = authorRepository.save(Author.builder()
                .name("Joshua Bloch")
                .biography("Java designer and author of Effective Java.")
                .nationality("American")
                .build());

        Author stephenHawking = authorRepository.save(Author.builder()
                .name("Stephen Hawking")
                .biography("Theoretical physicist and cosmologist.")
                .nationality("British")
                .build());

        // ── Books ──────────────────────────────────────────────────────────────
        Book cleanCode = bookRepository.save(Book.builder()
                .title("Clean Code: A Handbook of Agile Software Craftsmanship")
                .isbn("9780132350884")
                .description("A handbook of agile software craftsmanship. Packed with practical advice.")
                .publicationYear(2008)
                .language("English")
                .publisher("Prentice Hall")
                .category(programming)
                .authors(Set.of(robertMartin))
                .totalCopies(0)
                .availableCopies(0)
                .build());

        Book refactoring = bookRepository.save(Book.builder()
                .title("Refactoring: Improving the Design of Existing Code")
                .isbn("9780201485677")
                .description("A book on how to improve the design of existing code through refactoring.")
                .publicationYear(1999)
                .language("English")
                .publisher("Addison-Wesley")
                .category(programming)
                .authors(Set.of(martinFowler))
                .totalCopies(0)
                .availableCopies(0)
                .build());

        Book effectiveJava = bookRepository.save(Book.builder()
                .title("Effective Java")
                .isbn("9780134685991")
                .description("Best practices for the Java platform.")
                .publicationYear(2018)
                .language("English")
                .publisher("Addison-Wesley")
                .category(programming)
                .authors(Set.of(joshBloch))
                .totalCopies(0)
                .availableCopies(0)
                .build());

        Book briefHistory = bookRepository.save(Book.builder()
                .title("A Brief History of Time")
                .isbn("9780553380163")
                .description("Landmark volume in science writing by one of the great minds of our time.")
                .publicationYear(1988)
                .language("English")
                .publisher("Bantam Books")
                .category(science)
                .authors(Set.of(stephenHawking))
                .totalCopies(0)
                .availableCopies(0)
                .build());

        // ── Book Copies ────────────────────────────────────────────────────────
        addCopies(cleanCode,    List.of("CC-001", "CC-002", "CC-003"), "A1-01");
        addCopies(refactoring,  List.of("RF-001", "RF-002"),           "A1-02");
        addCopies(effectiveJava,List.of("EJ-001", "EJ-002", "EJ-003"), "A1-03");
        addCopies(briefHistory, List.of("BH-001", "BH-002"),           "B2-01");

        log.info("DataSeeder: seeding complete.");
        log.info("─────────────────────────────────────────────────────");
        log.info("  ADMIN     → admin@library.com       / Admin@123");
        log.info("  LIBRARIAN → librarian@library.com   / Lib@12345");
        log.info("  MEMBER    → member@library.com      / Member@123");
        log.info("─────────────────────────────────────────────────────");
    }

    private void addCopies(Book book, List<String> barcodes, String shelf) {
        int count = 0;
        for (String barcode : barcodes) {
            bookCopyRepository.save(BookCopy.builder()
                    .book(book)
                    .barcode(barcode)
                    .status(CopyStatus.AVAILABLE)
                    .shelfLocation(shelf)
                    .purchaseDate(LocalDate.now().minusMonths(6))
                    .build());
            count++;
        }
        book.setTotalCopies(barcodes.size());
        book.setAvailableCopies(barcodes.size());
        bookRepository.save(book);
    }
}