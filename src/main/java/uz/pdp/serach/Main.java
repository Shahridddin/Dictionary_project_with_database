package uz.pdp.serach;

import uz.pdp.config.SettingsConfig;

import java.sql.*;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    private static final String URL = "jdbc:postgresql://127.0.0.1:5432/f";
    private static final String USER = "postgres";
    private static final String PASSWORD = "2003";

    public static void main(String[] args) {
        while (true) {
            System.out.print("Enter the starting letter of the word (or 'exit' to quit): ");
            String startLetter = scanner.nextLine().trim();

            if (startLetter.equalsIgnoreCase("exit")) {
                System.out.println("Exiting...");
                break;
            }

            searchWordsByStartingLetter(startLetter);
        }
    }

    private static void searchWordsByStartingLetter(String startLetter) {
        if (startLetter.length() != 1) {
            System.out.println("Please enter a single letter.");
            return;
        }

        String sql = SettingsConfig.get("query.search");

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Prepare the LIKE pattern
            String pattern = startLetter + "%";
            statement.setString(1, pattern);

            try (ResultSet resultSet = statement.executeQuery()) {
                boolean found = false;
                while (resultSet.next()) {
                    String word = resultSet.getString("word");
                    String definition = resultSet.getString("defination");
                    System.out.println("=> " + word + " - " + definition);
                    found = true;
                }
                if (!found) {
                    System.out.println("No words found starting with " + startLetter);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
