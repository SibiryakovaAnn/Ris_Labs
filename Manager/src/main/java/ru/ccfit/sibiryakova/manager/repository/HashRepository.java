package ru.ccfit.sibiryakova.manager.repository;

import org.springframework.stereotype.Repository;
import ru.ccfit.sibiryakova.manager.models.Hash;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class HashRepository {
    private static final String DB_PATH = "/app/db/manager.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;

    public HashRepository() {
        initDatabase();
    }

    private void initDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS crack_hash (
                    request_id TEXT PRIMARY KEY,
                    hash TEXT NOT NULL,
                    max_length INTEGER NOT NULL
                )""");
        } catch (SQLException e) {
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public void save(String requestId, String hash, int maxLength) {
        String sql = "INSERT OR REPLACE INTO crack_hash VALUES(?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, requestId);
            pstmt.setString(2, hash);
            pstmt.setInt(3, maxLength);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save request", e);
        }
    }

    public void remove(String requestId) {
        String sql = "DELETE FROM crack_hash WHERE request_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, requestId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove request", e);
        }
    }

    public List<Hash> findAll() {
        List<Hash> results = new ArrayList<>();
        String sql = "SELECT hash, max_length FROM crack_hash";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                results.add(new Hash(
                        rs.getString("hash"),
                        rs.getInt("max_length")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load requests", e);
        }
        return results;
    }

}