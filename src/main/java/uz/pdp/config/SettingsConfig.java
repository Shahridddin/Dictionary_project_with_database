package uz.pdp.config;

import java.util.ResourceBundle;

public class SettingsConfig {
    private static final ResourceBundle settings = ResourceBundle.getBundle("dictionary");

    public static String get(String key) {
        return settings.getString(key);
    }
}

