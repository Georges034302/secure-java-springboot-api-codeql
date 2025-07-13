package com.example.UserApp.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.sql.*;

/**
 * REST controller for user operations.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    // Inject API key from config
    @Value("${app.api.key}")
    private String apiKey;

    /**
     * Retrieves a user by ID.
     *
     * @param id The user ID to search for.
     * @return A string with user info if found, otherwise "User not found".
     * @throws SQLException if the database connection or query fails.
     */
    @GetMapping("/{id}")
    public String getUserById(@PathVariable Long id) {
        String result = "";
        try {
            Connection conn = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
            // Use PreparedStatement to prevent SQL injection
            String sql = "SELECT * FROM \"user\" WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                result = "User: " + rs.getString("name") + ", Email: " + rs.getString("email");
            } else {
                result = "User not found";
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            result = "Error: " + e.getMessage();
        }
        return result;
    }
}