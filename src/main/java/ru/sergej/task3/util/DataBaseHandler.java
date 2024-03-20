package ru.sergej.task3.util;

import ru.sergej.task3.annotations.Column;
import ru.sergej.task3.annotations.Id;
import ru.sergej.task3.annotations.Table;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class DataBaseHandler {

    public void save (Connection connection, Object object) throws SQLException, IllegalArgumentException {

        Class<?> clazz = object.getClass();

        if (!clazz.isAnnotationPresent(Table.class)){
            throw new IllegalArgumentException("Класс не помечен аннотацией @Table");
        }

        Table table = clazz.getAnnotation(Table.class);
        String tableName = table.name();

        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);

            if(field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                columns.append(column.name()).append(",");
                values.append("?,");
            }
        }

        columns.deleteCharAt(columns.length() - 1);
        values.deleteCharAt(values.length() - 1);

        String sqlRequest = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, values);

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlRequest)) {
            int index = 1;
            for (Field field : fields) {
                if (field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(Id.class)) {
                    Object value = field.get(object);
                    preparedStatement.setObject(index++, value);
                }
            }
            preparedStatement.executeUpdate();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
