package pl.Model;

import pl.Utils.BCrypt;

import java.sql.*;
import java.util.ArrayList;

public class User {
    private int id;
    private String password;
    private String email;
    private String username;


    public User() {}


    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.setPassword(password);
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

    public void saveToDB(Connection conn) throws SQLException {
        if (this.id == 0) {
            String sql = "INSERT INTO users(username, email, password) VALUES (?, ?, ?)";
            String[] generatedColumns = { "ID" };

            PreparedStatement preparedStatement
                    = conn.prepareStatement(sql, generatedColumns);
            preparedStatement.setString(1, this.username);
            preparedStatement.setString(2, this.email);
            preparedStatement.setString(3, this.password);
            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
            }
        } else {
        String sql = "UPDATE users SET username=?, email=?, password=? where id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, this.username);
        preparedStatement.setString(2, this.email);
        preparedStatement.setString(3, this.password);
        preparedStatement.setInt(4, this.id);
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
            return loadedUser;}
        return null;
    }

    static public void printAllUsers(Connection conn) throws SQLException {
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
            users.add(loadedUser);}
        User[] uArray = new User[users.size()]; uArray = users.toArray(uArray);
        return uArray;
    }


    @Override
    public String toString() {
        return "id=" + id +
                "\t\tusername = " + username +
                "\t\temail = " + email +
                "\t\tpassword = " + password;
    }
}
