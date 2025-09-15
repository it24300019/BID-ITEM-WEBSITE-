package com.example.usermanagement.controller;

import com.example.usermanagement.entity.Student;
import com.example.usermanagement.repository.StudentRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class JavaFxStudentController implements Initializable {

    @FXML
    private TextField txtStudentId;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtAge;
    @FXML
    private Button btnSubmit;
    @FXML
    private Button btnLoad;
    @FXML
    private TextArea txtOutput;

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtOutput.setText("JavaFX + Spring Boot Integration Working!\n");
        txtOutput.appendText("Database connection established.\n");
        txtOutput.appendText("Ready to add/load students.\n\n");
    }

    @FXML
    private void handleSubmit() {
        try {
            // Get text and trim whitespace
            String idText = txtStudentId.getText().trim();
            String nameText = txtName.getText().trim();
            String ageText = txtAge.getText().trim();

            // Debug output to see what we're getting
            txtOutput.appendText("Debug - ID: '" + idText + "', Name: '" + nameText + "', Age: '" + ageText + "'\n");

            // Check for empty fields
            if (idText.isEmpty() || nameText.isEmpty() || ageText.isEmpty()) {
                txtOutput.appendText("Error: Please fill all fields!\n");
                return;
            }

            // Parse numbers
            Integer id = Integer.parseInt(idText);
            String name = nameText;
            Integer age = Integer.parseInt(ageText);

            // Create and save student
            Student student = new Student(id, name, age);
            studentRepository.save(student);

            txtOutput.appendText("✓ Student saved successfully: " + student.getName() + " (ID: " + id + ", Age: " + age + ")\n");
            clearFields();

        } catch (NumberFormatException e) {
            txtOutput.appendText("Number format error: " + e.getMessage() + "\n");
            txtOutput.appendText("Please make sure ID and Age contain only numbers.\n");
        } catch (Exception e) {
            txtOutput.appendText("Error saving student: " + e.getMessage() + "\n");
            e.printStackTrace(); // This will show detailed error in console
        }
    }

    @FXML
    private void handleLoad() {
        try {
            List<Student> students = studentRepository.findAll();
            txtOutput.setText("=== All Students ===\n");

            if (students.isEmpty()) {
                txtOutput.appendText("No students found in database.\n");
            } else {
                for (Student student : students) {
                    txtOutput.appendText("ID: " + student.getId() +
                            ", Name: " + student.getName() +
                            ", Age: " + student.getAge() + "\n");
                }
                txtOutput.appendText("\nTotal students: " + students.size() + "\n");
            }
            txtOutput.appendText("\n");

        } catch (Exception e) {
            txtOutput.appendText("Error loading students: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtStudentId.clear();
        txtName.clear();
        txtAge.clear();
        txtStudentId.requestFocus(); // Move cursor back to first field
    }
}
