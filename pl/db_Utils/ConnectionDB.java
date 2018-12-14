package pl.db_Utils;

import java.sql.*;


public class ConnectionDB {

    public static void main(String[] args) {
        ;
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = (Connection) DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/programmingSchool?useSSL=false&characterEncoding=utf8",
                "root",
                "coderslab");

        return connection;
    }

    public static void createTableUsers(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS users" +
                "(" +
                "id BIGINT AUTO_INCREMENT," +
                "username VARCHAR(255), " +
                "email VARCHAR(255) UNIQUE, " +
                "password VARCHAR(255)," +
                "user_group_id INT, " +
                "PRIMARY KEY (id), " +
                "FOREIGN KEY (user_group_id) REFERENCES user_group(id) " +
                "ON DELETE CASCADE" +
                ")";

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTableGroups(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS user_group" +
                "(" +
                "id INT AUTO_INCREMENT," +
                "name VARCHAR(255), " +
                "PRIMARY KEY (id)" +
                ")";

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTableExercise(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS exercise" +
                "(" +
                "id INT AUTO_INCREMENT," +
                "title VARCHAR(255), " +
                "description TEXT, " +
                "PRIMARY KEY (id)" +
                ")";

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTableSolition(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS solution" +
                "(" +
                "id INT AUTO_INCREMENT," +
                "created DATETIME, " +
                "updated DATETIME, " +
                "description TEXT, " +
                "exercise_id INT, " +
                "users_id BIGINT, " +
                "PRIMARY KEY (id), " +
                "FOREIGN KEY (exercise_id) REFERENCES exercise(id) " +
                "ON DELETE CASCADE," +
                "FOREIGN KEY (users_id) REFERENCES users(id) " +
                "ON DELETE CASCADE" +
                ")";

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
