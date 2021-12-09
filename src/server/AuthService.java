package server;

import java.sql.*;

public class AuthService {

    private static Connection connection;
    private static Statement statement;

    public static void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "student" , "student");

            System.out.println("Database connection successful\n");

            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int addUser(String nickname, String login, String pass) {
        try {
            String query = "INSERT INTO users (nickname, login, password) VALUES (?, ?, ?);";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, login);
            ps.setInt(2, pass.hashCode());
            ps.setString(3, nickname);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
