package com.ebidpro.controller;

import com.ebidpro.dto.LoginRequest;
import com.ebidpro.dto.RegisterRequest;
import com.ebidpro.dto.UserProfileResponse;
import com.ebidpro.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ebidpro.dto.ChangePasswordRequest;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        String result = userService.registerUser(registerRequest);
        if (result.equals("User registered successfully")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserProfileResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        return userService.loginUser(loginRequest)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @GetMapping("/profile/{email}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable String email) {
        return userService.getUserProfile(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profile/{email}")
    public ResponseEntity<String> updateUserProfile(@PathVariable String email, @Valid @RequestBody RegisterRequest updateRequest) {
        boolean updated = userService.updateUserProfile(email, updateRequest);
        if (updated) {
            return ResponseEntity.ok("Profile updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @DeleteMapping("/profile/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable String email) {
        boolean deleted = userService.deleteUser(email);
        if (deleted) {
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest req) {
        boolean changed = userService.changePassword(req.getEmail(), req.getOldPassword(), req.getNewPassword());
        if (changed) {
            return ResponseEntity.ok("Password changed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Current password is incorrect");
        }
    }

}
