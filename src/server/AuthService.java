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
            ps.setString(1, nickname);
            ps.setString(2, login);
            ps.setInt(3, pass.hashCode());
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getNicknameByLoginAndPass(String login, String pass) {
        String query = String.format("select nickname, password from users where login='%s'", login);
        try {
            ResultSet rs = statement.executeQuery(query); // возвращает выборку через select
            int myHash = pass.hashCode();
            // кеш числа 12345
            // изменим пароли в ДБ на хеш от строки pass1

            if (rs.next()) {
                String nick = rs.getString("nickname");
                int dbHash = rs.getInt("password");
                if (myHash == dbHash) {
                    return nick;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
