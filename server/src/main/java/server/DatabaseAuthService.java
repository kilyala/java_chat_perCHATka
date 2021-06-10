package server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAuthService implements AuthService {



    static Statement statement;

    private class UserData {
        String login;
        String password;
        String nickname;

        public UserData(String login, String password, String nickname) {
            this.login = login;
            this.password = password;
            this.nickname = nickname;
        }
    }


    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        String query = String.format("select nickname from chatUsers where login='%s' and password='%s'", login, password);
        try (ResultSet set = statement.executeQuery(query)) {
            if (set.next()) {
                return set.getString(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        try {
            String register = String.format("INSERT INTO chatUsers ('login', 'password','nickname') VALUES ('%s','%s','%s'); ", login, password, nickname);
            statement.execute(register);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

}