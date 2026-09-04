package com.pjsofttech.library.service;

import com.pjsofttech.library.dto.request.*;
import com.pjsofttech.library.model.Member;
import com.pjsofttech.library.model.MemberStatus;
import com.pjsofttech.library.model.Role;
import com.pjsofttech.library.model.User;
import com.pjsofttech.library.repository.MemberRepository;
import com.pjsofttech.library.repository.UserRepository;
import com.pjsofttech.library.dto.response.RegisterUserResponseDto;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MemberRepository memberRepository;

    //Helper Methods

    private User findUserById(Long id){
        return userRepository.findById(id).orElseThrow(()->new RuntimeException("User Not Found"));
    }
    private  User userReqDto_to_User(RegisterUserRequestDto registerUserRequestDto){
        return User.builder()
                .name(registerUserRequestDto.getName())
                .email(registerUserRequestDto.getEmail())
                .password(passwordEncoder.encode(registerUserRequestDto.getPassword()))
                .role(Role.MEMBER)
                .dateOfBirth(registerUserRequestDto.getDateOfBirth())
                .active(true)
                .build();
    }
    private static RegisterUserResponseDto user_to_userResponseDto(User user){
        return RegisterUserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .dateOfBirth(user.getDateOfBirth())
                .role(user.getRole())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
    private static List<RegisterUserResponseDto> listOfUsersToListOfResponseUsers(List<User> users){
        return users.stream()
                .map(user->RegisterUserResponseDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .dateOfBirth(user.getDateOfBirth())
                        .role(user.getRole())
                        .active(user.isActive())
                        .build()
                ).toList();
    }
    private String generateMembershipNumber() {
        String number;

        do {
            number = "MEM-" + UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();
        } while (memberRepository.existsByMembershipNumber(number));

        return number;
    }



    //Actual Methods
    @Transactional
    public RegisterUserResponseDto register(RegisterUserRequestDto registerUserRequestDto) {
        if (userRepository.existsByEmail(registerUserRequestDto.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        User user = userReqDto_to_User(registerUserRequestDto);
        userRepository.save(user);

        Member member = Member.builder()
                .phone(registerUserRequestDto.getPhone())
                .address(registerUserRequestDto.getAddress())
                .user(user)
                .membershipNumber(generateMembershipNumber())
                .membershipDate(LocalDate.now())
                .membershipExpiryDate(LocalDate.now().plusYears(1))
                .status(MemberStatus.ACTIVE)
                .build();
        memberRepository.save(member);
        return user_to_userResponseDto(user);
    }


    public List<RegisterUserResponseDto> fetchUsers() {
        return listOfUsersToListOfResponseUsers(userRepository.findAll());
    }

    public RegisterUserResponseDto fetchUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(()->new RuntimeException("User Not Found"));
        return user_to_userResponseDto(user);
    }

    public RegisterUserResponseDto updateUserById(Long id, UpdateUserRequestDto updateUserRequestDto) {
        User user = userRepository.findById(id).orElseThrow(()->new RuntimeException("User Not Found"));
        // Check if email is being changed
        if (!user.getEmail().equals(updateUserRequestDto.getEmail())
                && userRepository.existsByEmail(updateUserRequestDto.getEmail())) {
            throw new RuntimeException(
                    "Email already registered"
            );
        }
        user.setName(updateUserRequestDto.getName());
        user.setEmail(updateUserRequestDto.getEmail());
        user.setDateOfBirth(updateUserRequestDto.getDateOfBirth());
        User updatedUser = userRepository.save(user);
        return user_to_userResponseDto(updatedUser);
    }
    public String deleteUser(Long id){
        userRepository.deleteById(id);
        return "User with ID : "+id+" Successfully Deleted !";
    }

    public String changePassword(Long id, @Valid ChangePasswordRequestDto changePasswordRequestDto) {
        User user = userRepository.findById(id).orElseThrow(()->new RuntimeException("User Not Found"));
        if(!passwordEncoder.matches(
                changePasswordRequestDto.getCurrentPassword(),
                user.getPassword()
        )){
            throw new RuntimeException("Current password is incorrect");
        }
        // Don't allow same password
        if (passwordEncoder.matches(
                changePasswordRequestDto.getNewPassword(),
                user.getPassword())) {
            throw new RuntimeException(
                    "New password must be different from current password"
            );
        }
        user.setPassword(
                passwordEncoder.encode(changePasswordRequestDto.getNewPassword())
        );

        userRepository.save(user);
        return "Password Changed Successfully";
    }

    public RegisterUserResponseDto updateUserRole(
            Long id,
            AdminRoleUpdateDto dto) {
        User user = findUserById(id);
        user.setRole(dto.getRole());
        User updatedUser = userRepository.save(user);
        return user_to_userResponseDto(updatedUser);
    }

    public RegisterUserResponseDto updateUserStatus(
            Long id,
            UserStatusUpdateDto dto) {
        User user = findUserById(id);
        user.setActive(dto.isActive());
        User updatedUser = userRepository.save(user);
        return user_to_userResponseDto(updatedUser);
    }
    //*********************
    //Current User Operations
    //**********************
    //Get Current User
    public RegisterUserResponseDto getCurrentUser(
            String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));
        return user_to_userResponseDto(user);
    }

    //Update Current User
    public RegisterUserResponseDto updateCurrentUser(User loggedInUser, UpdateUserRequestDto updateUserRequestDto) {
        // Email is being changed
        if (!loggedInUser.getEmail().equals(updateUserRequestDto.getEmail())
                && userRepository.existsByEmail(updateUserRequestDto.getEmail())) {
            throw new RuntimeException(
                    "Email already registered"
            );
        }
        loggedInUser.setName(updateUserRequestDto.getName());
        loggedInUser.setEmail(updateUserRequestDto.getEmail());
        loggedInUser.setDateOfBirth(updateUserRequestDto.getDateOfBirth());
        userRepository.save(loggedInUser);
        return user_to_userResponseDto(loggedInUser);
    }

    //Change Password Of Current User
    public String changePassword(User loggedInUser,ChangePasswordRequestDto changePasswordRequestDto) {
        if(!passwordEncoder.matches(
                changePasswordRequestDto.getCurrentPassword(),
                loggedInUser.getPassword()
        )){
            throw new RuntimeException("Current password is incorrect");
        }
        // Don't allow same password
        if (passwordEncoder.matches(
                changePasswordRequestDto.getNewPassword(),
                loggedInUser.getPassword())) {
            throw new RuntimeException(
                    "New password must be different from current password"
            );
        }
        loggedInUser.setPassword(
                passwordEncoder.encode(changePasswordRequestDto.getNewPassword())
        );

        userRepository.save(loggedInUser);
        return "Password Changed Successfully";
    }
}
