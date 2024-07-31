package uz.pdp.service;

import uz.pdp.config.SettingsConfig;
import uz.pdp.user.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static uz.pdp.config.DataSourceConfig.getConnection;

public class UserService {

    public void insertUser(User user) {
        String selectSql = SettingsConfig.get("query.user.selectOne");
        String insertSql = SettingsConfig.get("query.user.insert");

        // userId bormi yo`qmi shuni check qiladi
        if (extracted(user, selectSql)) return;

        // bo`lmasa insert qiladi
        try (Connection connection = getConnection();
             PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {

            insertStatement.setLong(1, user.getUserId());
            insertStatement.setString(2, user.getUserName());
            insertStatement.setString(3, user.getPassword());
            insertStatement.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
            insertStatement.executeUpdate();
            System.out.println("User inserted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error inserting user.");
        }
    }

    public User selectOne(Long userId) {
        String sql = SettingsConfig.get("query.user.selectOne");
        User user = null;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    user = new User();
                    user.setUserId(resultSet.getLong("user_id"));
                    user.setUserName(resultSet.getString("user_name"));
                    user.setPassword(resultSet.getString("password"));
                    user.setCreatedAt(resultSet.getTimestamp("created_at"));
                } else {
                    System.out.println("User not found with ID: " + userId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error finding user.");
        }

        return user;
    }


    public List<User> selectAllUser() {
        List<User> users = new ArrayList<>();
        String sql = SettingsConfig.get("query.user.selectAll");

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                User user = new User();
                user.setUserId(resultSet.getLong("user_id"));
                user.setUserName(resultSet.getString("user_name"));
                user.setPassword(resultSet.getString("password"));
                user.setCreatedAt(resultSet.getDate("created_at"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error retrieving users.");
        }

        return users;
    }

    public void deleteUser(Long userId) {
        String sql = SettingsConfig.get("query.user.delete");

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, userId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User deleted successfully.");
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error deleting user.");
        }
    }

    public void updateUser(User user) {
        String sql = SettingsConfig.get("query.user.update");

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getUserName());
            statement.setString(2, user.getPassword());
            statement.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
            statement.setLong(4, user.getUserId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User updated successfully.");
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating user.");
        }
    }

    private boolean extracted(User user, String selectSql) {
        try (Connection connection = getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {

            selectStatement.setLong(1, user.getUserId());

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("This user_id already exists, enter another user_id");
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error checking user existence.");
            return true;
        }
        return false;
    }
}
