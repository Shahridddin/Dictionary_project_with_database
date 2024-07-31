package uz.pdp.login;

import uz.pdp.config.SettingsConfig;

import java.sql.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static uz.pdp.config.DataSourceConfig.getConnection;

public class Login {

    private static final Logger LOGGER = Logger.getLogger(Login.class.getName());

    public static boolean login(String userName, String password) {
        String url = SettingsConfig.get("query.user.login");
        try(Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(url)){
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    String storedPassword = rs.getString("password");
                    if (storedPassword.equals(password)) {
                        return true;
                    }
                }
            }
        }catch (SQLException e){
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return false;
    }

    public static Long getUserId(String password) {
        String url = SettingsConfig.get("query.user.id.by.password");
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(url)) {
            ps.setString(1, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("user_id");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return null;
    }

}
