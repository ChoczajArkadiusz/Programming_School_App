package pl.Model;

import pl.Utils.ScannerHelper;
import pl.db_Utils.ConnectionDB;

import java.sql.*;
import java.util.ArrayList;

public class Exercise {
    private int id;
    private String title;
    private String description;


    public static void main(String[] args) {

        Connection connection = null;
        try {
            connection = ConnectionDB.getConnection();
            boolean exit = false;
            while (!exit) {
                printAllExercises(connection);
                System.out.println("\n" +
                        "Wybierz jedną z opcji:\n" +
                        "add    - dodanie nowego zadania\n" +
                        "edit   - edycja zadania z bazy\n" +
                        "delete - usunięcie zadania z bazy\n" +
                        "exit   - zakończenie działania programu\n");

                String userChoice = ScannerHelper.getLine("");
                switch (userChoice) {
                    case "add":
                        Exercise newExercise = new Exercise();
                        String newExerciseTitle = ScannerHelper.getLine("Podaj tytuł nowego użytkownika: ").trim();
                        newExercise.setTitle(newExerciseTitle);
                        String newDescription = ScannerHelper.getLine("Podaj opis nowego użytkownika: ").trim();
                        newExercise.setDescription(newDescription);
                        newExercise.saveToDB(connection);
                        break;
                    case "edit":
                        int editExerciseId = ScannerHelper.getInt("Podaj numer id zadania, które chcesz edytować: ");
                        Exercise editExercise = loadExerciseById(connection, editExerciseId);
                        printExerciseById(connection, editExerciseId);
                        String editUsername = ScannerHelper.getLine("Podaj nowy tytuł zadania o id: " + editExerciseId).trim();
                        editExercise.setTitle(editUsername);
                        String editEmail = ScannerHelper.getLine("Podaj nowy opis zadania o id: " + editExerciseId).trim();
                        editExercise.setDescription(editEmail);
                        editExercise.saveToDB(connection);
                        break;
                    case "delete":
                        int deleteExerciseId = ScannerHelper.getInt("Podaj numer id użytkownika, którego chcesz edytować: ");
                        Exercise deleteExercise = loadExerciseById(connection, deleteExerciseId);
                        printExerciseById(connection, deleteExerciseId);
                        deleteExercise.delete(connection);
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


    public Exercise() {
    }

    public Exercise(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void saveToDB(Connection conn) throws SQLException {
        if (this.id == 0) {
            String sql = "INSERT INTO exersice(title, description) VALUES (?, ?)";
            String[] generatedColumns = {"ID"};

            PreparedStatement preparedStatement
                    = conn.prepareStatement(sql, generatedColumns);
            preparedStatement.setString(1, this.title);
            preparedStatement.setString(2, this.description);
            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
            }
        } else {
            String sql = "UPDATE exersice SET title=?, description=? where id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, this.title);
            preparedStatement.setString(2, this.description);
            preparedStatement.setInt(3, this.id);
            preparedStatement.executeUpdate();
        }
    }

    public void delete(Connection conn) throws SQLException {
        if (this.id != 0) {
            String sql = "DELETE FROM exercise WHERE id=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, this.id);
            preparedStatement.executeUpdate();
            this.id = 0;
        }
    }


    static public void printExerciseById(Connection conn, int id) throws SQLException {
        Exercise exerciseRead = loadExerciseById(conn, id);
        System.out.println(exerciseRead);
    }


    static public Exercise loadExerciseById(Connection conn, int id) throws SQLException {
        String sql = "SELECT * FROM exercise where id=?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            Exercise loadedExercise = new Exercise();
            loadedExercise.id = resultSet.getInt("id");
            loadedExercise.title = resultSet.getString("title");
            loadedExercise.description = resultSet.getString("description");
            return loadedExercise;
        }
        return null;
    }


    static public void printAllExercises(Connection conn) throws SQLException {
        System.out.println("\n ***** Lista zadań: ");
        Exercise[] exercises = loadAllExercises(conn);
        for (Exercise exercise : exercises) {
            System.out.println(exercise);
        }
    }

    static public Exercise[] loadAllExercises(Connection conn) throws SQLException {
        ArrayList<Exercise> exercise = new ArrayList<Exercise>();
        String sql = "SELECT * FROM exesercise";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Exercise loadedExercise = new Exercise();
            loadedExercise.id = resultSet.getInt("id");
            loadedExercise.title = resultSet.getString("title");
            loadedExercise.description = resultSet.getString("description");
            exercise.add(loadedExercise);
        }
        Exercise[] uArray = new Exercise[exercise.size()];
        uArray = exercise.toArray(uArray);
        return uArray;
    }


    @Override
    public String toString() {
        return "id=" + id +
                "\t\ttytuł = " + title +
                "\t\topis = " + description;
    }

}
