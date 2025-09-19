package com.ebidpro.dto;

public class UserProfileResponse {
    private String fullName;
    private String email;
    private String phone;
    private String role;

    public UserProfileResponse(String fullName, String email, String phone, String role) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    // Getters
    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getRole() {
        return role;
    }
}
