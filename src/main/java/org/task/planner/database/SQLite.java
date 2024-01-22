package org.task.planner.database;

import org.task.planner.model.Task;
import org.task.planner.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLite {
    public static User insertUser(String login, String password) throws SQLException {
        Connection connection = DatabaseManager.getConnection();

        String insertSQL = "INSERT INTO user (login, password) VALUES (?, ?) RETURNING id, login, password";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setLogin(resultSet.getString("login"));
                    user.setPassword(resultSet.getString("password"));
                    return user;
                } else {
                    throw new SQLException("Failed to retrieve the generated user.");
                }
            }
        }
    }

    public static User validateUser(String login, String password) throws SQLException {
        Connection connection = DatabaseManager.getConnection();

        String validateUserSql = "SELECT * FROM user WHERE login=? AND password=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(validateUserSql)) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);

            return getUser(preparedStatement);
        }
    }

    public static User getUserByUsername(String login) {
        Connection connection = DatabaseManager.getConnection();
        String validateUserSql = "SELECT * FROM user WHERE login=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(validateUserSql)) {
            preparedStatement.setString(1, login);

            return getUser(preparedStatement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static User getUser(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setLogin(resultSet.getString("login"));
                user.setPassword(resultSet.getString("password"));
                return user;
            } else {
                // return null or throw an exception
                return null;
            }
        }
    }

    public static void insertTask(Integer userId, Task task) throws SQLException {
        Connection connection = DatabaseManager.getConnection();

        String insertSQL = "INSERT INTO task (userId, category, name, description, status, targetDate) VALUES (?, ?, ?, ?, ?, ?) RETURNING id, userId, category, name, description, status, targetDate";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, task.getCategory());
            preparedStatement.setString(3, task.getName());
            preparedStatement.setString(4, task.getDescription());
            preparedStatement.setBoolean(5, task.isStatus());
            preparedStatement.setString(6, task.getTargetDate());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Task newTask = new Task();
                    newTask.setId(resultSet.getInt("id"));
                    newTask.setUserId(resultSet.getInt("userId"));
                    newTask.setCategory(resultSet.getString("category"));
                    newTask.setName(resultSet.getString("name"));
                    newTask.setDescription(resultSet.getString("description"));
                    newTask.setStatus(resultSet.getBoolean("status"));
                    newTask.setTargetDate(resultSet.getString("targetDate"));

                } else {
                    throw new SQLException("Failed to retrieve the generated key.");
                }
            }
        }
    }

    public static void changeTaskStatus(Integer taskId, boolean status) throws SQLException {
        Connection connection = DatabaseManager.getConnection();

        String updateSQL = "UPDATE task SET status=? WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setBoolean(1, status);
            preparedStatement.setInt(2, taskId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No rows were updated. Task with taskId " + taskId + " not found.");
            }
        }
    }


    public static List<Task> getActiveUserTasks(Integer userId) throws SQLException {
        Connection connection = DatabaseManager.getConnection();

        List<Task> tasks = new ArrayList<>();
        String selectSQL = "SELECT * FROM task WHERE userId = ? AND status=TRUE";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Task task = new Task();
                    task.setId(resultSet.getInt("id"));
                    task.setUserId(resultSet.getInt("userId"));
                    task.setCategory(resultSet.getString("category"));
                    task.setName(resultSet.getString("name"));
                    task.setDescription(resultSet.getString("description"));
                    task.setStatus(resultSet.getBoolean("status"));
                    task.setTargetDate(resultSet.getString("targetDate"));

                    tasks.add(task);
                }
            }
        }

        return tasks;
    }
}
