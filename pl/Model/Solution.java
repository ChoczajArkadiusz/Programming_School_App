package pl.Model;

import pl.Utils.ScannerHelper;
import pl.db_Utils.ConnectionDB;

import java.sql.*;
import java.util.ArrayList;


public class Solution {
    private int id;
    private Date created;
    private Date updated;
    private String description;
    private int exercise_id;
    private int user_id;


    public static void main(String[] args) {

        Connection connection = null;
        try {
            connection = ConnectionDB.getConnection();
            boolean exit = false;
            while (!exit) {
                System.out.println("\n" +
                        "Wybierz jedną z opcji:\n" +
                        "add    - przypisanie zadań d użytkownika\n" +
                        "view   - podgląd rozwiązań użytkownika\n" +
                        "exit   - zakończenie działania programu\n");

                String userChoice = ScannerHelper.getLine("");
                switch (userChoice) {
                    case "add":
                        User.printAllUsers(connection);
                        int addSolutionUserId = ScannerHelper.getInt("Podaj numer id użytkownika: ");
                        Exercise.printExerciseById(connection, addSolutionUserId);
                        int addSolutionExerciseId = ScannerHelper.getInt("Podaj numer id zadania: ");
                        Solution newSolution = new Solution();
                        newSolution.setUser_id(addSolutionUserId);
                        newSolution.setExercise_id(addSolutionExerciseId);
                        java.util.Date utilDate = new java.util.Date();
                        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                        newSolution.setCreated(sqlDate);
                        newSolution.saveToDB(connection);
                        break;
                    case "view":
                        User.printAllUsers(connection);
                        int viewSolutionId = ScannerHelper.getInt("Podaj numer id użytkownika, którego rozwiązania chcesz zobaczyć: ");
                        Solution viewSolution = loadSolutionById(connection, viewSolutionId);
                        printSolutionById(connection, viewSolutionId);
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


    public Solution() {
    }

    public Solution(Date created, Date updated, String description, int exercise_id, int user_id) {
        this.created = created;
        this.updated = updated;
        this.description = description;
        this.exercise_id = exercise_id;
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getExercise_id() {
        return exercise_id;
    }

    public void setExercise_id(int exercise_id) {
        this.exercise_id = exercise_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void saveToDB(Connection conn) throws SQLException {
        if (this.id == 0) {
            String sql = "INSERT INTO exercise(created, updated, description, exercise_id, users_id) VALUES (?, ?, ?, ?, ?)";
            String[] generatedColumns = {"ID"};

            PreparedStatement preparedStatement
                    = conn.prepareStatement(sql, generatedColumns);
            preparedStatement.setDate(1, this.created);
            preparedStatement.setDate(2, this.updated);
            preparedStatement.setString(3, this.description);
            preparedStatement.setInt(4, this.exercise_id);
            preparedStatement.setInt(5, this.user_id);
            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
            }
        } else {
            String sql = "UPDATE exercise SET created=?, updated=?, description=?, exercise_id=?, users_id=? where id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setDate(1, this.created);
            preparedStatement.setDate(2, this.updated);
            preparedStatement.setString(3, this.description);
            preparedStatement.setInt(4, this.exercise_id);
            preparedStatement.setInt(5, this.user_id);
            preparedStatement.setInt(6, this.id);
            preparedStatement.executeUpdate();
        }
    }


    public void delete(Connection conn) throws SQLException {
        if (this.id != 0) {
            String sql = "DELETE FROM exercises WHERE id=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, this.id);
            preparedStatement.executeUpdate();
            this.id = 0;
        }
    }


    static public void printSolutionById(Connection conn, int id) throws SQLException {
        Solution solutionRead = loadSolutionById(conn, id);
        System.out.println(solutionRead);
    }


    static public Solution loadSolutionById(Connection conn, int id) throws SQLException {
        String sql = "SELECT * FROM solution where id=?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            Solution loadedSolution = new Solution();
            loadedSolution.id = resultSet.getInt("id");
            loadedSolution.created = resultSet.getDate("created");
            loadedSolution.updated = resultSet.getDate("updated");
            loadedSolution.description = resultSet.getString("description");
            loadedSolution.exercise_id = resultSet.getInt("exercise_id");
            loadedSolution.user_id = resultSet.getInt("user_id");
            return loadedSolution;
        }
        return null;
    }


    static public Solution[] loadAllSolutions(Connection conn) throws SQLException {
        ArrayList<Solution> solutions = new ArrayList<Solution>();
        String sql = "SELECT * FROM solution";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Solution loadedSolution = new Solution();
            loadedSolution.id = resultSet.getInt("id");
            loadedSolution.created = resultSet.getDate("created");
            loadedSolution.updated = resultSet.getDate("updated");
            loadedSolution.description = resultSet.getString("description");
            loadedSolution.exercise_id = resultSet.getInt("exercise_id");
            loadedSolution.user_id = resultSet.getInt("user_id");
            solutions.add(loadedSolution);
        }
        Solution[] uArray = new Solution[solutions.size()];
        uArray = solutions.toArray(uArray);
        return uArray;
    }


    static public Solution[] loadAllByUserId(Connection conn, int user_id) throws SQLException {
        ArrayList<Solution> solutions = new ArrayList<Solution>();
        String sql = "SELECT * FROM solution WHERE user_id=?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, user_id);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Solution loadedSolution = new Solution();
            loadedSolution.id = resultSet.getInt("id");
            loadedSolution.created = resultSet.getDate("created");
            loadedSolution.updated = resultSet.getDate("updated");
            loadedSolution.description = resultSet.getString("description");
            loadedSolution.exercise_id = resultSet.getInt("exercise_id");
            loadedSolution.user_id = resultSet.getInt("user_id");
            solutions.add(loadedSolution);
        }
        Solution[] uArray = new Solution[solutions.size()];
        uArray = solutions.toArray(uArray);
        return uArray;
    }


    static public Solution[] loadAllByExerciseId(Connection conn, int exercise_id) throws SQLException {
        ArrayList<Solution> solutions = new ArrayList<Solution>();
        String sql = "SELECT * FROM solution WHERE exercise_id=? ORDER BY updated DESC";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, exercise_id);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Solution loadedSolution = new Solution();
            loadedSolution.id = resultSet.getInt("id");
            loadedSolution.created = resultSet.getDate("created");
            loadedSolution.updated = resultSet.getDate("updated");
            loadedSolution.description = resultSet.getString("description");
            loadedSolution.exercise_id = resultSet.getInt("exercise_id");
            loadedSolution.user_id = resultSet.getInt("user_id");
            solutions.add(loadedSolution);
        }
        Solution[] uArray = new Solution[solutions.size()];
        uArray = solutions.toArray(uArray);
        return uArray;
    }


    @Override
    public String toString() {
        return "id=" + id +
                "\t\tutworzono = " + created +
                "\t\taktualizacja = " + updated +
                "\t\topis = " + description +
                "\t\tid zadania = " + exercise_id +
                "\t\tid użytkownika = " + user_id;
    }

}
