package ru.sergej;

import ru.sergej.util.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {

        createTable();
    }

    private static void createTable() {
        try (Connection connection = ConnectionManager.open()) {
            Statement statement = connection.createStatement();
            statement.execute("""
                                    create table if not exists person (
                                        id int,
                                        first_name varchar(256),
                                        second_name varchar(256)           
                                    )
                    """);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}