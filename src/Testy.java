import pl.Model.User;
import pl.db_Utils.ConnectionDB;

import java.sql.Connection;
import java.sql.SQLException;

public class Testy {
    public static void main(String[] args) {
        User user1 = new User();
        user1.setUsername("Jan");
        user1.setEmail("jan.kowalski@gmail.com");
        user1.setPassword("1234");

        User user2 = new User();
        user2.setUsername("Tomasz");
        user2.setEmail("tomasz.nowak@gmail.com");
        user2.setPassword("5678");


        Connection connection = null;
        try {
            connection = ConnectionDB.getConnection();
            ConnectionDB.createTableUsers(connection);
            ConnectionDB.createTableGroups(connection);
            ConnectionDB.createTableExercise(connection);
            ConnectionDB.createTableSolition(connection);
//            user1.saveToDB(connection);

            User userRead1 = User.loadUserById(connection, 3);
            User userRead2 = User.loadUserById(connection, 2);
            System.out.println(userRead1.toString());
            System.out.println(userRead2.toString());

//            userRead1.setUsername("Janusz");
//            userRead1.setEmail("janusz.kowalski@gmail.com");
//            userRead1.setPassword("1234");
//            userRead1.saveToDB(connection);

//            userRead1.delete(connection);

            User[] users = User.loadAllUsers(connection);
            System.out.println("\n ***** TABLICA użytkowników: ");
            for (User user : users) {
                System.out.println(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
