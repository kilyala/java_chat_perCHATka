package server;

import java.sql.*;

public class DatabaseUsers {

    static final String DATABASE_URL = "jdbc:sqlite:usersdb.db";
    static Connection connection;
    static Statement statement;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DATABASE_URL);
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void init() throws SQLException {
        DatabaseUsers databaseUsers = new DatabaseUsers();
        databaseUsers.createTable();
        databaseUsers.insertNewUser("testL", "testP", "testN");
        databaseUsers.insertNewUser("testL1", "testP1", "testN1");
        databaseUsers.insertNewUser("testL2", "testP2", "testN2");
        databaseUsers.insertNewUserPS("testLps", "testPps", "testNps");
        databaseUsers.userSearch();
//        databaseUsers.dropTable();
    }

    public void createTable() throws SQLException {
        String createTable = "create table chatUsers (" +
                "login text not null, " +
                "password text not null, " +
                "nickname text not null)";
        statement.execute(createTable);
    }

//    public void createTable() throws SQLException {
//        String createTable = "create table chatUsers (login text not null, " +
//                "password text not null, " +
//                "nickname text not null)";
//        statement.execute(createTable);
//    }

    public void insertNewUser(String login, String password, String nickname) throws SQLException {
        String insertSql = "insert into chatUsers (login, password, nickname) values ('" +login+ "', " +
                "'" +password+ "', " +
                "'" +nickname+ "')";
        statement.execute(insertSql);
    }

    public boolean insertNewUserPS(String login, String password, String nickname) {
        System.out.println("insertNewUserPS start try: login"+login+" " + "password " +password+ "nickname " +nickname);
        try (PreparedStatement preparedStatement = connection.prepareStatement("insert into chatUsers" +
                " (login, password, nickname) values (?, ?, ?)")) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, nickname);
            return !preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void userSearch() throws SQLException {
        String us = "select * from chatUsers";
        ResultSet resultSet = statement.executeQuery(us);
        while (resultSet.next()) {
            System.out.println(resultSet.getString(1) + " " +
                    resultSet.getString(2) + " " +
                    resultSet.getString(3));
        }
    }

    public static void dropTable() throws SQLException {
        String dropSql = "drop table chatUsers";
        statement.execute(dropSql);
    }

    synchronized static String getNickname(String login, String password) {
        String temp = "";
        String query = String.format("select nickname from chatUsers where login='%s' and password='%s'", login, password);
        try (ResultSet set = statement.executeQuery(query)) {
            if (set.next()) {
                temp = set.getString(1);
                System.out.println(temp);
                return temp;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}