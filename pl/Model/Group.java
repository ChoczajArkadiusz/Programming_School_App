package pl.Model;

import pl.Utils.ScannerHelper;
import pl.db_Utils.ConnectionDB;

import java.sql.*;
import java.util.ArrayList;

public class Group {
    private int id;
    private String name;


    public static void main(String[] args) {

        Connection connection = null;
        try {
            connection = ConnectionDB.getConnection();
            boolean exit = false;
            while (!exit) {
                printAllGroups(connection);
                System.out.println("\n" +
                        "Wybierz jedną z opcji:\n" +
                        "add    - dodanie nowej grupy\n" +
                        "edit   - edycja grupy z bazy\n" +
                        "delete - usunięcie grupy z bazy\n" +
                        "exit   - zakończenie działania programu\n");

                String userChoice = ScannerHelper.getLine("");
                switch (userChoice) {
                    case "add":
                        Group newGroup = new Group();
                        String newName = ScannerHelper.getLine("Podaj nazwę nowej grupy: ").trim();
                        newGroup.setName(newName);
                        newGroup.saveToDB(connection);
                        break;
                    case "edit":
                        int editGroupId = ScannerHelper.getInt("Podaj numer id grupy, którą chcesz edytować: ");
                        Group editGroup = loadGroupById(connection, editGroupId);
                        printGroupById(connection, editGroupId);
                        String editName = ScannerHelper.getLine("Podaj nową nazwę grupy o id: " + editGroupId).trim();
                        editGroup.setName(editName);
                        editGroup.saveToDB(connection);
                        break;
                    case "delete":
                        int deleteGroupId = ScannerHelper.getInt("Podaj numer id użytkownika, którego chcesz usunąć: ");
                        Group deleteGroup = loadGroupById(connection, deleteGroupId);
                        printGroupById(connection, deleteGroupId);
                        deleteGroup.delete(connection);
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


    public Group() {
    }

    public Group(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void saveToDB(Connection conn) throws SQLException {
        if (this.id == 0) {
            String sql = "INSERT INTO user_group(name) VALUES (?)";
            String[] generatedColumns = {"ID"};

            PreparedStatement preparedStatement
                    = conn.prepareStatement(sql, generatedColumns);
            preparedStatement.setString(1, this.name);
            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
            }
        } else {
            String sql = "UPDATE user_group SET name=? where id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, this.name);
            preparedStatement.setInt(2, this.id);
            preparedStatement.executeUpdate();
        }
    }


    public void delete(Connection conn) throws SQLException {
        String userChoice = ScannerHelper.getLine("Czy na pewno chcesz usunąć wybraną grupę?\n" +
                "T  - tak,\n" +
                "N  - nie,\n ");
        if (this.id != 0  &&  userChoice.equals("T")) {
            String sql = "DELETE FROM user_group WHERE id=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, this.id);
            preparedStatement.executeUpdate();
            this.id = 0;
        }
    }


    static public void printGroupById(Connection conn, int id) throws SQLException {
        Group group = loadGroupById(conn, id);
        System.out.println(group);
    }


    static public Group loadGroupById(Connection conn, int id) throws SQLException {
        String sql = "SELECT * FROM user_group where id=?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            Group loadedGroup = new Group();
            loadedGroup.id = resultSet.getInt("id");
            loadedGroup.name = resultSet.getString("name");
            return loadedGroup;
        }
        return null;
    }


    static public void printAllGroups(Connection conn) throws SQLException {
        System.out.println("\n ***** Lista grup: ");
        Group[] groups = loadAllGroups(conn);
        for (Group group : groups) {
            System.out.println(group);
        }
    }


    static public Group[] loadAllGroups(Connection conn) throws SQLException {
        ArrayList<Group> groups = new ArrayList<Group>();
        String sql = "SELECT * FROM user_group";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Group loadedGroup = new Group();
            loadedGroup.id = resultSet.getInt("id");
            loadedGroup.name = resultSet.getString("name");
            groups.add(loadedGroup);
        }
        Group[] uArray = new Group[groups.size()];
        uArray = groups.toArray(uArray);
        return uArray;
    }


    @Override
    public String toString() {
        return "id=" + id +
                "\t\tnazwa = " + name;
    }

}
