package com.paypal.desk;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbHelper {

    private static final Connection connection = getConnection();

    private static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/paypal",
                    "root",
                    "root"
            );

            System.out.println("Connection successful");
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    static int createUser(String firstName, String lastName) {
        String sqlInsert = "insert into users " +
                "(first_name, last_name)" +
                " values (" +
                "'" + firstName + "'" +
                ", " +
                "'" + lastName + "'" +
                ")";

        try {
            Statement statement = connection.createStatement();
            statement.execute(sqlInsert);

            String idSql = "select max(id) from users";
            Statement idStatement = connection.createStatement();
            ResultSet resultSet = idStatement.executeQuery(idSql);

            resultSet.next();

            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    static void cashFlow(int userId, double amount) {
        String sql = "update users set balance = balance+" +
                amount +
                "where id = " +
                userId;
        try {
            Statement idStatement = connection.createStatement();
            int countUpdated = idStatement.executeUpdate(sql);
            System.out.println(countUpdated + " records affected.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static int transaction(int userFrom, int userTo, double amount) {
        String sqlInsert = "insert into transactions " +
                "(user_from,  user_to, transaction_amount)" +
                " values (" +
                "'" + userFrom + "'" +
                ", " +
                "'" + userTo + "'" +
                ", " +
                "'" + amount + "'" +
                ")";

        try {
            Statement statement = connection.createStatement();
            statement.execute(sqlInsert);
            cashFlow(userFrom, -amount);
            cashFlow(userTo, amount);
            String idSql = "select max(id) from users";
            Statement idStatement = connection.createStatement();
            ResultSet resultSet = idStatement.executeQuery(idSql);
            resultSet.next();

            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

    }

    static List<User> listUsers() {
        String sql = "select * from users";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            List<User> userList = new ArrayList<>();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                double balance = resultSet.getDouble("balance");

                userList.add(new User(
                        id, firstName, lastName, balance
                ));
            }
            return userList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
