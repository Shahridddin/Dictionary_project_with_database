package uz.pdp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import uz.pdp.service.UserService;
import uz.pdp.service.WordService;
import uz.pdp.user.User;
import uz.pdp.word.Word;

import java.sql.Timestamp;
import java.util.*;

import static uz.pdp.login.Login.getUserId;
import static uz.pdp.login.Login.login;

public class Main {
    static Scanner scannerInt = new Scanner(System.in);
    static Scanner scannerStr = new Scanner(System.in);

    public static void main(String[] args) {
        int step = 10;
        while (step > 0) {
            System.out.println("1.User. 2.Login user.");
            System.out.print("Enter step: ");
            String stepStr = scannerStr.nextLine();
            Integer stepInt = convertStringToInt(stepStr);

            if (stepInt == null || stepInt < 0 || stepInt > 2) {
                System.out.println("Please enter type number.");
                continue;
            }
            if (stepInt == 0) {
                System.out.println("Exiting program.");
                break;
            }

            switch (stepInt) {
                case 1 -> userInfo();
                case 2 -> {
                    System.out.print("Enter username: ");
                    String userName = scannerStr.nextLine();
                    System.out.print("Enter password: ");
                    String password = scannerStr.nextLine();
                    boolean isAuthenticated = login(userName, password);
                    if (isAuthenticated) {
                        System.out.println("Login successful!");
                        Long currentUser_Id = getUserId(password);
                        System.out.println("User ID: " + currentUser_Id);
                        infoWord(currentUser_Id);
                    } else {
                        System.out.println("Login does not exists? Please check your username and password.");
                    }
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public static void userInfo() {
        UserService userService = new UserService();
        int a = 5;
        while (a > 0) {
            System.out.println("1.Insert user. 2.Select one user. 3.Select all user. " +
                    "4.Delete user. 5.Update user. 0.Exit!");
            System.out.println("----------------------------------------");
            System.out.print("Enter option: ");
            a = scannerInt.nextInt();
            switch (a) {
                case 1 -> insertUser(userService);
                case 2 -> selectOneUser(userService);
                case 3 -> selectAllUsers(userService);
                case 4 -> deleteUser(userService);
                case 5 -> updateUser(userService);
                case 0 -> System.out.println("Exiting user info.");
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void insertUser(UserService userService) {
        User user = new User();
        System.out.print("Enter userId: ");
        String userIdStr = scannerStr.nextLine();
        Long userId = convertStringToLong(userIdStr);
        if (userId != null) {
            user.setUserId(userId);
            System.out.print("Enter username: ");
            user.setUserName(scannerStr.nextLine());
            user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            System.out.print("Enter user password: ");
            user.setPassword(scannerStr.nextLine());
            userService.insertUser(user);
        }
    }

    private static void selectOneUser(UserService userService) {
        System.out.print("Enter userId: ");
        String userIdStr = scannerStr.nextLine();
        Long userId = convertStringToLong(userIdStr);
        if (userId != null) {
            User user = userService.selectOne(userId);
            if (user != null) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(user);
                System.out.println(json);
            } else {
                System.out.println("User not found.");
            }
        }
    }

    private static void selectAllUsers(UserService userService) {
        List<User> users = userService.selectAllUser();
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonOutput = gson.toJson(users);
            System.out.println(jsonOutput);
        }
    }

    private static void deleteUser(UserService userService) {
        System.out.print("Enter delete userId: ");
        String userIdStr = scannerStr.nextLine();
        Long userId = convertStringToLong(userIdStr);
        if (userId != null) {
            userService.deleteUser(userId);
        }
    }

    private static void updateUser(UserService userService) {
        User user = new User();
        System.out.print("Enter userId: ");
        String userIdStr = scannerStr.nextLine();
        Long userId = convertStringToLong(userIdStr);
        if (userId != null) {
            user.setUserId(userId);
            System.out.print("Enter update userName: ");
            user.setUserName(scannerStr.nextLine());
            System.out.print("Enter update password: ");
            user.setPassword(scannerStr.nextLine());
            user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            userService.updateUser(user);
        }
    }

    public static void infoWord(Long currentUser_Id) {
        WordService wordService = new WordService();
        int a = 5;
        while (a > 0) {
            System.out.println("1.Insert word. 2.Select all word. 3.Delete word. 4.Update word. 0.Exit!");
            System.out.println("----------------------------------------");
            System.out.print("Enter option: ");
            a = scannerInt.nextInt();
            switch (a) {
                case 1 -> insertWord(wordService);
                case 2 -> selectAllWords(wordService, currentUser_Id);
                case 3 -> deleteWord(wordService);
                case 4 -> updateWord(wordService);
                case 0 -> System.out.println("Exiting word info.");
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void insertWord(WordService wordService) {
        Word word = new Word();
        System.out.print("Enter user_id: ");
        String userIdStr = scannerStr.nextLine();
        Long userId = convertStringToLong(userIdStr);
        if (userId != null) {
            word.setUserId(userId);
            System.out.print("Enter word: ");
            word.setWord(scannerStr.nextLine().toLowerCase());
            System.out.print("Enter defination: ");
            word.setDefinition(scannerStr.nextLine().toLowerCase());
            word.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            wordService.insertWord(word);
        }
    }

    private static void selectAllWords(WordService wordService, Long currentUser_Id) {
        List<String> words = wordService.selectAllWords(currentUser_Id);
        if (words.isEmpty()) {
            System.out.println("No words found for user ID: " + currentUser_Id);
        } else {
            List<Map<String, String>> wordList = new ArrayList<>();
            for (int i = 0; i < words.size(); i += 2) {
                Map<String, String> wordMap = new HashMap<>();
                wordMap.put("word", words.get(i));
                wordMap.put("defination", words.get(i + 1));
                wordList.add(wordMap);
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonOutput = gson.toJson(wordList);
            System.out.println(jsonOutput);
        }
    }

    private static void deleteWord(WordService wordService) {
        System.out.print("Enter word id: ");
        String wordIdStr = scannerStr.nextLine();
        Long wordId = convertStringToLong(wordIdStr);
        if (wordId != null) {
            wordService.deleteWord(wordId);
        }
    }

    private static void updateWord(WordService wordService) {
        Word word = new Word();
        System.out.print("Enter word id: ");
        String wordIdStr = scannerStr.nextLine();
        Long wordId = convertStringToLong(wordIdStr);
        if (wordId != null) {
            word.setId(wordId);
            System.out.print("Enter user_id: ");
            String userIdStr = scannerStr.nextLine();
            Long userId = convertStringToLong(userIdStr);
            if (userId != null) {
                word.setUserId(userId);
                System.out.print("Enter word: ");
                word.setWord(scannerStr.nextLine());
                System.out.print("Enter defination: ");
                word.setDefinition(scannerStr.nextLine());
                word.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                wordService.updateWord(word);
            }
        }
    }

    private static Long convertStringToLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            System.out.println("Please enter type number.");
            return null;
        }
    }
    private static Integer convertStringToInt(String str) {
        try{
            return Integer.parseInt(str);
        }catch (NumberFormatException e){
            return null;
        }
    }
}
