package com.example.usermanagement;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class JavaFxSpringBootApp extends Application {

    private ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        // Launch JavaFX application - NO PRELOADER PROPERTY
        Application.launch(JavaFxSpringBootApp.class, args);
    }

    @Override
    public void init() throws Exception {
        // Initialize Spring Boot context
        springContext = SpringApplication.run(UserManagementApplication.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Load FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/user-management.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);

            Parent root = fxmlLoader.load();

            // Create scene
            Scene scene = new Scene(root, 800, 900);

            // Setup primary stage
            primaryStage.setTitle("User Management System - JavaFX + Spring Boot");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(600);
            primaryStage.setMinHeight(700);

            // Center the window on screen
            primaryStage.centerOnScreen();

            // Show the window
            primaryStage.show();

            // Print success message to console
            System.out.println("🎉 User Management System Started Successfully!");
            System.out.println("📊 Database: Connected to SQL Server");
            System.out.println("🖥️ JavaFX: User interface loaded");
            System.out.println("🌐 REST API: Available at http://localhost:8080");

        } catch (Exception e) {
            System.err.println("❌ Error starting JavaFX application: " + e.getMessage());
            e.printStackTrace();
            Platform.exit();
        }
    }

    @Override
    public void stop() throws Exception {
        // Clean shutdown
        if (springContext != null) {
            springContext.close();
        }
        Platform.exit();
        System.out.println("👋 User Management System Shutdown Complete");
    }
}
