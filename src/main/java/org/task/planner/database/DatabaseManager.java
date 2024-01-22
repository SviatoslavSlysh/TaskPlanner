package org.task.planner.database;

import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String JDBC_URL = "jdbc:sqlite:database.db";
    @Getter
    private static final Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(JDBC_URL);
            createTables(connection);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to establish a database connection.", e);
        }
    }

    private static void createTables(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String createUser = "CREATE TABLE IF NOT EXISTS user (id INTEGER PRIMARY KEY AUTOINCREMENT, login TEXT, password TEXT)";
            statement.executeUpdate(createUser);

            String createTask = "CREATE TABLE IF NOT EXISTS task (id INTEGER PRIMARY KEY AUTOINCREMENT, userId INTEGER, category TEXT, name TEXT, description TEXT, status BOOLEAN, targetDate TEXT, FOREIGN KEY (userId) REFERENCES user(id))";
            statement.executeUpdate(createTask);
        }
    }
}
