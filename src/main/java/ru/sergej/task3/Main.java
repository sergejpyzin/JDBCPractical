package ru.sergej.task3;

import ru.sergej.task3.entity.Person;
import ru.sergej.task3.repositories.DataBaseHandler;
import ru.sergej.task3.util.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        try (Connection connection = ConnectionManager.open()) {
            DataBaseHandler dataBaseHandler = new DataBaseHandler();


            Person person1 = new Person("Иван", "Иванов", 30);
            dataBaseHandler.save(connection, person1);
            System.out.println("Person сохранен в базе данных: " + person1);


            Person personToUpdate = new Person("Петр", "Петров", 25);
            dataBaseHandler.save(connection, personToUpdate);
            System.out.println("Person сохранен в базе данных: " + personToUpdate);

            personToUpdate.setAge(26);
            dataBaseHandler.update(personToUpdate, connection);
            System.out.println("Person обновлен в базе данных: " + personToUpdate);
        } catch (SQLException | IllegalArgumentException | IllegalAccessException e) {
            System.out.println(e.getMessage());
        }

    }
}
