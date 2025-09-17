package com.example.usermanagement.controller;

import com.example.usermanagement.entity.User;
import com.example.usermanagement.repository.UserRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class UserManagementController implements Initializable {

    // FXML Controls (from your existing UI)
    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtFullName;
    @FXML private TextField txtAge;
    @FXML private ComboBox<String> cmbGender;
    @FXML private TextField txtPhoneNumber;
    @FXML private ComboBox<User.UserRole> cmbUserRole;
    @FXML private TextArea txtPreferences;
    @FXML private CheckBox chkIsActive;

    @FXML private Button btnSave;
    @FXML private Button btnUpdate;
    @FXML private Button btnClear;

    @FXML private TextField txtSearch;
    @FXML private Button btnSearch;
    @FXML private ComboBox<User.UserRole> cmbFilterRole;
    @FXML private Button btnLoadAllUsers;

    @FXML private TextArea txtOutput;

    @Autowired
    private UserRepository userRepository;

    // Simple session management - current logged-in user
    private static User currentLoggedInUser = null;

    // Current user being edited
    private User currentUser = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComboBoxes();
        displayWelcomeMessage();
        loadInitialData();
    }

    private void setupComboBoxes() {
        // Setup Gender ComboBox
        cmbGender.setItems(FXCollections.observableArrayList(
                "Male", "Female", "Other", "Prefer not to say"
        ));

        // Setup User Role ComboBox (no admin role for this module)
        cmbUserRole.setItems(FXCollections.observableArrayList(User.UserRole.values()));
        cmbUserRole.setValue(User.UserRole.BIDDER);

        // Setup Filter Role ComboBox
        cmbFilterRole.setItems(FXCollections.observableArrayList(User.UserRole.values()));
    }

    // USER REGISTRATION
    @FXML
    private void handleSaveUser() {
        try {
            // Validate required fields
            if (!validateRequiredFields()) {
                return;
            }

            // Check if username already exists
            if (userRepository.existsByUsername(txtUsername.getText().trim())) {
                showError("Username already exists! Please choose a different username.");
                return;
            }

            // Check if email already exists
            if (userRepository.existsByEmail(txtEmail.getText().trim())) {
                showError("Email already exists! Please choose a different email.");
                return;
            }

            // Create new user
            User newUser = createUserFromForm();
            User savedUser = userRepository.save(newUser);

            // Success message
            txtOutput.appendText("✅ USER REGISTRATION SUCCESSFUL!\n");
            txtOutput.appendText("   👤 Username: " + savedUser.getUsername() + "\n");
            txtOutput.appendText("   📧 Email: " + savedUser.getEmail() + "\n");
            txtOutput.appendText("   🎭 Role: " + savedUser.getUserRole().getDisplayName() + "\n");
            txtOutput.appendText("   🆔 User ID: " + savedUser.getUserId() + "\n");
            txtOutput.appendText("   ✅ Status: Active\n\n");

            txtOutput.appendText("🎉 You can now login with your credentials!\n\n");

            clearForm();

        } catch (Exception e) {
            showError("Error saving user: " + e.getMessage());
        }
    }

    // USER LOGIN
    @FXML
    private void handleLogin() {
        try {
            String username = txtUsername.getText().trim();
            String password = txtPassword.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                showError("Please enter both username and password!");
                return;
            }

            // Find user by username
            Optional<User> userOpt = userRepository.findByUsername(username);

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                // Check if account is active
                if (!user.isActive()) {
                    showError("Account is deactivated. Please contact support.");
                    return;
                }

                // Simple password check (use BCrypt in production)
                if (user.getPasswordHash().equals(password)) {
                    // Login successful
                    currentLoggedInUser = user;
                    currentUser = user;

                    txtOutput.appendText("✅ LOGIN SUCCESSFUL!\n");
                    txtOutput.appendText("   👤 Welcome: " + user.getFullName() + "\n");
                    txtOutput.appendText("   🎭 Role: " + user.getUserRole().getDisplayName() + "\n");
                    txtOutput.appendText("   📧 Email: " + user.getEmail() + "\n\n");

                    // Load user's profile data into form
                    loadUserProfile(user);

                    txtOutput.appendText("📝 You can now update your profile or delete your account.\n\n");

                } else {
                    showError("Incorrect password!");
                }
            } else {
                showError("Username not found!");
            }

        } catch (Exception e) {
            showError("Login error: " + e.getMessage());
        }
    }

    // UPDATE PROFILE (only for logged-in user)
    @FXML
    private void handleUpdateUser() {
        try {
            if (currentLoggedInUser == null) {
                showError("Please login first to update your profile!");
                return;
            }

            if (!validateRequiredFields()) {
                return;
            }

            // Update current user's information
            updateUserFromForm(currentLoggedInUser);
            User updatedUser = userRepository.save(currentLoggedInUser);

            txtOutput.appendText("✅ PROFILE UPDATE SUCCESSFUL!\n");
            txtOutput.appendText("   👤 Name: " + updatedUser.getFullName() + "\n");
            txtOutput.appendText("   📧 Email: " + updatedUser.getEmail() + "\n");
            txtOutput.appendText("   📱 Phone: " + (updatedUser.getPhoneNumber() != null ? updatedUser.getPhoneNumber() : "Not provided") + "\n\n");

        } catch (Exception e) {
            showError("Error updating profile: " + e.getMessage());
        }
    }

    // DELETE OWN ACCOUNT
    @FXML
    private void handleDeleteMyAccount() {
        try {
            if (currentLoggedInUser == null) {
                showError("Please login first!");
                return;
            }

            // Confirmation dialog
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("⚠️ Delete Account");
            confirmDialog.setHeaderText("Are you sure you want to delete your account?");
            confirmDialog.setContentText(
                    "This will deactivate your account:\n\n" +
                            "👤 Username: " + currentLoggedInUser.getUsername() + "\n" +
                            "📧 Email: " + currentLoggedInUser.getEmail() + "\n" +
                            "👨‍💼 Name: " + currentLoggedInUser.getFullName() + "\n\n" +
                            "⚠️ You will not be able to login after this!\n" +
                            "✅ Your data will be preserved for records\n\n" +
                            "Are you absolutely sure?"
            );

            Optional<ButtonType> result = confirmDialog.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Soft delete - mark as inactive
                currentLoggedInUser.setIsActive(false);
                userRepository.save(currentLoggedInUser);

                txtOutput.appendText("✅ ACCOUNT DELETION SUCCESSFUL!\n");
                txtOutput.appendText("   🗑️ Account deactivated: " + currentLoggedInUser.getFullName() + "\n");
                txtOutput.appendText("   📅 Deletion Date: " + java.time.LocalDateTime.now() + "\n");
                txtOutput.appendText("   ℹ️ Your data is preserved for records\n");
                txtOutput.appendText("   👋 Thank you for using our service. Goodbye!\n\n");

                // Logout and clear form
                currentLoggedInUser = null;
                currentUser = null;
                clearForm();

            } else {
                txtOutput.appendText("❌ Account deletion cancelled.\n\n");
            }

        } catch (Exception e) {
            showError("Error deleting account: " + e.getMessage());
        }
    }

    // LOGOUT
    @FXML
    private void handleLogout() {
        if (currentLoggedInUser != null) {
            txtOutput.appendText("👋 Logged out: " + currentLoggedInUser.getFullName() + "\n\n");
            currentLoggedInUser = null;
            currentUser = null;
            clearForm();
        } else {
            showError("No user is currently logged in!");
        }
    }

    // SEARCH USERS (view only)
    @FXML
    private void handleSearchUsers() {
        try {
            String searchTerm = txtSearch.getText().trim();
            User.UserRole filterRole = cmbFilterRole.getValue();

            List<User> users;

            if (!searchTerm.isEmpty() && filterRole != null) {
                users = userRepository.findActiveUsersByNameContaining(searchTerm);
                users = users.stream()
                        .filter(user -> user.getUserRole() == filterRole)
                        .toList();
            } else if (!searchTerm.isEmpty()) {
                users = userRepository.findActiveUsersByNameContaining(searchTerm);
            } else if (filterRole != null) {
                users = userRepository.findActiveUsersByRole(filterRole);
            } else {
                users = userRepository.findAllActiveUsers();
            }

            displayUsers(users, "Search Results");

        } catch (Exception e) {
            showError("Error searching users: " + e.getMessage());
        }
    }

    @FXML
    private void handleLoadAllUsers() {
        try {
            List<User> users = userRepository.findAllActiveUsers();
            displayUsers(users, "All Active Users");
        } catch (Exception e) {
            showError("Error loading users: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearForm() {
        clearForm();
        txtOutput.appendText("🧹 Form cleared. Ready for new registration or login.\n\n");
    }

    // Helper methods
    private boolean validateRequiredFields() {
        if (txtUsername.getText().trim().isEmpty()) {
            showError("Username is required!");
            txtUsername.requestFocus();
            return false;
        }

        if (txtEmail.getText().trim().isEmpty()) {
            showError("Email is required!");
            txtEmail.requestFocus();
            return false;
        }

        if (txtPassword.getText().trim().isEmpty()) {
            showError("Password is required!");
            txtPassword.requestFocus();
            return false;
        }

        if (txtFullName.getText().trim().isEmpty()) {
            showError("Full Name is required!");
            txtFullName.requestFocus();
            return false;
        }

        if (!txtEmail.getText().trim().contains("@")) {
            showError("Please enter a valid email address!");
            txtEmail.requestFocus();
            return false;
        }

        return true;
    }

    private User createUserFromForm() {
        User user = new User();
        updateUserFromForm(user);
        return user;
    }

    private void updateUserFromForm(User user) {
        user.setUsername(txtUsername.getText().trim());
        user.setEmail(txtEmail.getText().trim());
        user.setPasswordHash(txtPassword.getText().trim()); // Use BCrypt in production
        user.setFullName(txtFullName.getText().trim());

        if (!txtAge.getText().trim().isEmpty()) {
            try {
                user.setAge(Integer.parseInt(txtAge.getText().trim()));
            } catch (NumberFormatException e) {
                // Ignore invalid age input
            }
        }

        user.setGender(cmbGender.getValue());
        user.setPhoneNumber(txtPhoneNumber.getText().trim());
        user.setUserRole(cmbUserRole.getValue());
        user.setPreferences(txtPreferences.getText().trim());
        user.setIsActive(chkIsActive.isSelected());
    }

    private void loadUserProfile(User user) {
        txtUsername.setText(user.getUsername());
        txtEmail.setText(user.getEmail());
        txtPassword.clear(); // Don't show password for security
        txtFullName.setText(user.getFullName());
        txtAge.setText(user.getAge() != null ? user.getAge().toString() : "");
        cmbGender.setValue(user.getGender());
        txtPhoneNumber.setText(user.getPhoneNumber());
        cmbUserRole.setValue(user.getUserRole());
        txtPreferences.setText(user.getPreferences());
        chkIsActive.setSelected(user.isActive());
    }

    private void displayUsers(List<User> users, String title) {
        txtOutput.appendText("📋 === " + title + " ===\n\n");

        if (users.isEmpty()) {
            txtOutput.appendText("No active users found.\n\n");
            return;
        }

        for (User user : users) {
            txtOutput.appendText("🆔 ID: " + user.getUserId() + "\n");
            txtOutput.appendText("👤 Username: " + user.getUsername() + "\n");
            txtOutput.appendText("👨‍💼 Name: " + user.getFullName() + "\n");
            txtOutput.appendText("🎭 Role: " + user.getUserRole().getDisplayName() + "\n");
            txtOutput.appendText("📧 Email: " + user.getEmail() + "\n");
            txtOutput.appendText("✅ Status: Active\n");
            txtOutput.appendText("─────────────────────────────────────\n");
        }

        txtOutput.appendText("\n📊 Total: " + users.size() + " user(s)\n\n");
    }

    private void displayWelcomeMessage() {
        txtOutput.setText("🎉 USER SELF-MANAGEMENT SYSTEM\n");
        txtOutput.appendText("📊 Database connection established\n");
        txtOutput.appendText("✅ Ready for user registration and login\n\n");

        txtOutput.appendText("🔹 REGISTRATION: Fill form and click 'Save User'\n");
        txtOutput.appendText("🔹 LOGIN: Enter username/password and click 'Update User' (acts as login)\n");
        txtOutput.appendText("🔹 UPDATE PROFILE: Login first, then modify and update\n");
        txtOutput.appendText("🔹 DELETE ACCOUNT: Available after login\n");
        txtOutput.appendText("🔹 SEARCH: View other active users\n\n");
    }

    private void loadInitialData() {
        try {
            long totalUsers = userRepository.countActiveUsers();
            txtOutput.appendText("📈 SYSTEM STATISTICS:\n");
            txtOutput.appendText("   👥 Active Users: " + totalUsers + "\n\n");
        } catch (Exception e) {
            txtOutput.appendText("⚠️ Could not load statistics: " + e.getMessage() + "\n\n");
        }
    }

    private void clearForm() {
        txtUsername.clear();
        txtEmail.clear();
        txtPassword.clear();
        txtFullName.clear();
        txtAge.clear();
        cmbGender.setValue(null);
        txtPhoneNumber.clear();
        cmbUserRole.setValue(User.UserRole.BIDDER);
        txtPreferences.clear();
        chkIsActive.setSelected(true);
        txtSearch.clear();
        cmbFilterRole.setValue(null);

        txtUsername.requestFocus();
    }

    private void showError(String message) {
        txtOutput.appendText("❌ ERROR: " + message + "\n\n");
    }// Move cursor back to first field
}
