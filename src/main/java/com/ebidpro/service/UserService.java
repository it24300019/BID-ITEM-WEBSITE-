package com.ebidpro.service;

import com.ebidpro.dto.LoginRequest;
import com.ebidpro.dto.RegisterRequest;
import com.ebidpro.dto.UserProfileResponse;
import com.ebidpro.entity.User;
import com.ebidpro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public String registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return "Email already exists";
        }

        User newUser = new User();
        newUser.setFullName(registerRequest.getFullName());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setPhone(registerRequest.getPhone());
        newUser.setRole(registerRequest.getRole());
        userRepository.save(newUser);
        return "User registered successfully";
    }

    public Optional<UserProfileResponse> loginUser(LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
                // Update last login time
                user.setLastLogin(new Date());
                userRepository.save(user);
                return Optional.of(new UserProfileResponse(user.getFullName(), user.getEmail(), user.getPhone(), user.getRole()));
            }
        }
        return Optional.empty();
    }

    public Optional<UserProfileResponse> getUserProfile(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        return userOpt.map(user -> new UserProfileResponse(user.getFullName(), user.getEmail(), user.getPhone(), user.getRole()));
    }

    public boolean updateUserProfile(String email, RegisterRequest updateRequest) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Update allowed fields
            user.setFullName(updateRequest.getFullName());
            user.setPhone(updateRequest.getPhone()); // ✅ This should update phone

            // Email update not allowed for security
            // Password update only if not dummy
            if (updateRequest.getPassword() != null &&
                    !updateRequest.getPassword().equals("dummy_password")) {
                user.setPasswordHash(passwordEncoder.encode(updateRequest.getPassword()));
            }

            // Update timestamp
            user.setUpdatedAt(new Date());

            // Save to database
            userRepository.save(user);
            return true;
        }
        return false;
    }


    public boolean deleteUser(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            userRepository.delete(userOpt.get());
            return true;
        }
        return false;
    }

    public boolean changePassword(String email, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
                user.setPasswordHash(passwordEncoder.encode(newPassword));
                user.setUpdatedAt(new Date());
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

}
