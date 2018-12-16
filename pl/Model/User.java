package pl.Model;

import pl.Utils.BCrypt;
import pl.Utils.ScannerHelper;
import pl.db_Utils.ConnectionDB;

import java.sql.*;
import java.util.ArrayList;

public class User {
    private int id;
    private String password;
    private String email;
    private String username;
    private int user_group_id;


    public static void main(String[] args) {

        Connection connection = null;
        try {
            connection = ConnectionDB.getConnection();
            boolean exit = false;
            while (!exit) {
                printAllUsers(connection);
                System.out.println("\n" +
                        "Wybierz jedną z opcji:\n" +
                        "add    - dodanie nowego użytkownika\n" +
                        "edit   - edycja użytkownika z bazy\n" +
                        "delete - usunięcie użytkownika z bazy\n" +
                        "exit   - zakończenie działania programu\n");

                String userChoice = ScannerHelper.getLine("");
                switch (userChoice) {
                    case "add":
                        User newUser = new User();
                        String newUsername = ScannerHelper.getLine("Podaj nazwę nowego użytkownika: ").trim();
                        newUser.setUsername(newUsername);
                        String newEmail = ScannerHelper.getLine("Podaj adres e-mail nowego użytkownika: ").trim();
                        newUser.setEmail(newEmail);
                        String newPassword = ScannerHelper.getLine("Podaj hasło nowego użytkownika: ").trim();
                        newUser.setPassword(newPassword);
                        int newGroupId = ScannerHelper.getInt("Podaj numer grupy dla nowego użytkownika: ");
                        newUser.setUser_group_id(newGroupId);
                        newUser.saveToDB(connection);
                        break;
                    case "edit":
                        int editUserId = ScannerHelper.getInt("Podaj numer id użytkownika, którego chcesz edytować: ");
                        User editUser = loadUserById(connection, editUserId);
                        printUserById(connection, editUserId);
                        String editUsername = ScannerHelper.getLine("Podaj nową nazwę użytkownika o id: " + editUserId).trim();
                        editUser.setUsername(editUsername);
                        String editEmail = ScannerHelper.getLine("Podaj nowy adres e-mail użytkownika o id: " + editUserId).trim();
                        editUser.setEmail(editEmail);
                        String editPassword = ScannerHelper.getLine("Podaj nowe hasło użytkownika o id: " + editUserId).trim();
                        editUser.setPassword(editPassword);
                        int editGroupId = ScannerHelper.getInt("Podaj nowy numer grupy dla użytkownika o id: " + editUserId);
                        editUser.setUser_group_id(editGroupId);
                        editUser.saveToDB(connection);
                        break;
                    case "delete":
                        int deleteUserId = ScannerHelper.getInt("Podaj numer id użytkownika, którego chcesz edytować: ");
                        User deleteUser = loadUserById(connection, deleteUserId);
                        printUserById(connection, deleteUserId);
                        deleteUser.delete(connection);
                        break;
                    case "exit":
                        exit = true;
                        System.out.println("Zakńcznie działania programu.");
                        break;
                    default:
                        System.out.println("Nie rozpoznano polecenia");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public User() {
    }


    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.setPassword(password);
    }

    public int getId() {
        return id;
    }

    public void setPassword(String password) {
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getUser_group_id() {
        return user_group_id;
    }

    public void setUser_group_id(int user_group_id) {
        this.user_group_id = user_group_id;
    }


    public void saveToDB(Connection conn) throws SQLException {
        if (this.id == 0) {
            String sql = "INSERT INTO users(username, email, password, user_group_id) VALUES (?, ?, ?, ?)";
            String[] generatedColumns = {"ID"};

            PreparedStatement preparedStatement
                    = conn.prepareStatement(sql, generatedColumns);
            preparedStatement.setString(1, this.username);
            preparedStatement.setString(2, this.email);
            preparedStatement.setString(3, this.password);
            preparedStatement.setInt(4, this.user_group_id);
            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
            }
        } else {
            String sql = "UPDATE users SET username=?, email=?, password=?, user_group_id=? where id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, this.username);
            preparedStatement.setString(2, this.email);
            preparedStatement.setString(3, this.password);
            preparedStatement.setInt(4, this.user_group_id);
            preparedStatement.setInt(5, this.id);
            preparedStatement.executeUpdate();
        }
    }

    public void delete(Connection conn) throws SQLException {
        if (this.id != 0) {
            String sql = "DELETE FROM users WHERE id=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, this.id);
            preparedStatement.executeUpdate();
            this.id = 0;
        }
    }

    static public void printUserById(Connection conn, int id) throws SQLException {
        User userRead = loadUserById(conn, id);
        System.out.println(userRead);
    }

    static public User loadUserById(Connection conn, int id) throws SQLException {
        String sql = "SELECT * FROM users where id=?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            User loadedUser = new User();
            loadedUser.id = resultSet.getInt("id");
            loadedUser.username = resultSet.getString("username");
            loadedUser.password = resultSet.getString("password");
            loadedUser.email = resultSet.getString("email");
            loadedUser.user_group_id = resultSet.getInt("user_group_id");
            return loadedUser;
        }
        return null;
    }

    static public void printAllUsers(Connection conn) throws SQLException {
        System.out.println("\n ***** Lista użytkowników: ");
        User[] users = loadAllUsers(conn);
        for (User user : users) {
            System.out.println(user);
        }
    }

    static public User[] loadAllUsers(Connection conn) throws SQLException {
        ArrayList<User> users = new ArrayList<User>();
        String sql = "SELECT * FROM users";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            User loadedUser = new User();
            loadedUser.id = resultSet.getInt("id");
            loadedUser.username = resultSet.getString("username");
            loadedUser.password = resultSet.getString("password");
            loadedUser.email = resultSet.getString("email");
            loadedUser.user_group_id = resultSet.getInt("user_group_id");
            users.add(loadedUser);
        }
        User[] uArray = new User[users.size()];
        uArray = users.toArray(uArray);
        return uArray;
    }


    static public User[] loadAllByGroupId(Connection conn, int user_group_id) throws SQLException {
        ArrayList<User> users = new ArrayList<User>();
        String sql = "SELECT * FROM users WHERE user_group_id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, user_group_id);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            User loadedUser = new User();
            loadedUser.id = resultSet.getInt("id");
            loadedUser.username = resultSet.getString("username");
            loadedUser.password = resultSet.getString("password");
            loadedUser.email = resultSet.getString("email");
            loadedUser.user_group_id = resultSet.getInt("user_group_id");
            users.add(loadedUser);
        }
        User[] uArray = new User[users.size()];
        uArray = users.toArray(uArray);
        return uArray;
    }


    @Override
    public String toString() {
        return "id=" + id +
                "\t\tnazwa = " + username +
                "\t\temail = " + email +
                "\t\thasło = " + password;
    }
}
