package ru.sergej.tasks1_2;

import ru.sergej.task3.util.ConnectionManager;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = ConnectionManager.open()) {
            acceptConnection(connection);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    private static void acceptConnection(Connection connection) throws SQLException {
        dropTable(connection, "person");
        createTable(connection);
        insertDataToTable(connection);
        requestDataFromDatabase(connection);
        updateTable(connection, "Sergey", "Holmogorov");
        requestDataFromDatabase(connection);
        deletedRowInTable(connection, 2);
        requestDataFromDatabase(connection);
        requestDataFromDatabase(connection);
    }

    private static void dropTable(Connection connection, String tableName) throws SQLException {
        try(Statement statement = connection.createStatement()){
            String sqlRequest = "drop table if exists " + tableName;
            statement.execute(sqlRequest);
        }
    }

    private static void requestDataFromDatabase(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String sqlRequest = "select id, first_name, second_name from person";
            ResultSet resultSet = statement.executeQuery(sqlRequest);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String fullName = resultSet.getString("first_name") + " " + resultSet.getString("second_name");

                System.out.printf("Персона: id - %d, Полное имя - %s\n", id, fullName);

            }
        }
    }

    private static void deletedRowInTable(Connection connection, int id) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String sqlRequest = "delete from person where id = " + id;
            System.out.println("Было удалено: " + statement.executeUpdate(sqlRequest));
        }

    }

    private static void updateTable(Connection connection, String first_name, String second_name) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                update person set second_name = ? where first_name = ?""")) {
            preparedStatement.setString(1, second_name);
            preparedStatement.setString(2, first_name);
            preparedStatement.executeUpdate();
        }


    }

    private static void insertDataToTable(Connection connection) throws SQLException {

        try (Statement statement = connection.createStatement()) {
            int affectedRow = statement.executeUpdate("""
                    insert into person (id, first_name, second_name) values 
                    (1, "Igor", "Semenov"),
                    (2, "Sergey", "Crus"),
                    (3, "Petr", "Crusenshtern"),
                    (4, "Anna", "Palka"),
                    (5, "Vadim", "Vadimov"),
                    (6, "Elena", "Luck")
                    """);

            System.out.println("В таблицу было добавлено: " + affectedRow + " строк.");
        }
    }

    private static void createTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                                    create table if not exists person (
                                        id int,
                                        first_name varchar(256),
                                        second_name varchar(256)           
                                    )
                    """);
        }
    }
}