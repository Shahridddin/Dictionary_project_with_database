package uz.pdp.service;

import uz.pdp.config.SettingsConfig;
import uz.pdp.word.Word;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static uz.pdp.config.DataSourceConfig.getConnection;

public class WordService {

    public void insertWord(Word word) {
        // SQL query to check if the word already exists
        String checkSql = SettingsConfig.get("query.wor.check");
        String insertSql = SettingsConfig.get("query.wor.insert");

        try (Connection connection = getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(checkSql)) {

            // Check if the word already exists
            checkStatement.setString(1, word.getWord());

            try (ResultSet resultSet = checkStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    System.out.println("Bu so`z databaseda bor.");
                    return;
                }
            }

            // Databaseda bo`lmasa
            try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                insertStatement.setLong(1, word.getUserId());
                insertStatement.setString(2, word.getWord());
                insertStatement.setString(3, word.getDefinition());
                insertStatement.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
                insertStatement.executeUpdate();
                System.out.println("Word inserted successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error inserting word.");
        }
    }


    public List<String> selectAllWords(Long userId) {
        List<String> words = new ArrayList<>();
        String sql = SettingsConfig.get("query.wor.selectAll");

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String word = resultSet.getString("word");
                    String definition = resultSet.getString("defination");
                    words.add(word);
                    words.add(definition);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error selectAllWords method:" + userId + ": " + e.getMessage());
        }

        return words;
    }


    public void deleteWord(Long id) {
        String sql = SettingsConfig.get("query.wor.delete");

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Word deleted successfully.");
            } else {
                System.out.println("Word not found or already deleted.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error deleting word.");
        }
    }

    public void updateWord(Word word) {
        String sql = SettingsConfig.get("query.wor.update");

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, word.getUserId());
            statement.setString(2, word.getWord());
            statement.setString(3, word.getDefinition());
            statement.setDate(4, new java.sql.Date(word.getCreatedAt().getTime()));
            statement.setLong(5, word.getId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Word updated successfully.");
            } else {
                System.out.println("Word not found or no changes made.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating word.");
        }
    }
}
