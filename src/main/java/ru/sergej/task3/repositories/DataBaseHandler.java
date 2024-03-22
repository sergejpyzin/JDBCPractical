package ru.sergej.task3.repositories;

import ru.sergej.task3.annotations.Column;
import ru.sergej.task3.annotations.Id;
import ru.sergej.task3.annotations.Table;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class DataBaseHandler {

    public void save(Connection connection, Object object) throws SQLException, IllegalArgumentException {
        Class<?> clazz = object.getClass();

        if (!clazz.isAnnotationPresent(Table.class)) {
            throw new IllegalArgumentException("Класс не помечен аннотацией @Table");
        }

        Table table = clazz.getAnnotation(Table.class);
        String tableName = table.name();

        if (!isTableExists(connection, tableName)) {
            createTable(connection, clazz);
        }

        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(Column.class)) {
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
                if (field.isAnnotationPresent(Column.class)) {
                    Object value = field.get(object);
                    preparedStatement.setObject(index++, value);
                }
            }
            preparedStatement.executeUpdate();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Object object, Connection connection) throws SQLException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        if (!clazz.isAnnotationPresent(Table.class)) {
            throw new IllegalArgumentException("Класс не помечен аннотацией @Table");
        }

        Table table = clazz.getAnnotation(Table.class);
        String tableName = table.name();

        if (!isTableExists(connection, tableName)) {
            createTable(connection, clazz);
        }

        StringBuilder setClause = new StringBuilder();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                setClause.append(column.name()).append("=?,");
            }
        }

        setClause.deleteCharAt(setClause.length() - 1);

        if (!setClause.toString().isEmpty()) {
            String sql = String.format("UPDATE %s SET %s WHERE id=?", tableName, setClause);

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                int index = 1;
                for (Field field : fields) {
                    if (field.isAnnotationPresent(Column.class)) {
                        Object value = field.get(object);
                        preparedStatement.setObject(index++, value);
                    }
                }
                preparedStatement.setObject(index, getIdValue(object));
                preparedStatement.executeUpdate();
            }
        }
    }

    private Object getIdValue(Object obj) throws IllegalAccessException {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Id.class)) {
                return field.get(obj);
            }
        }
        return null;
    }

    private boolean isTableExists(Connection connection, String tableName) throws SQLException {
        boolean tableExists = false;
        try (ResultSet resultSet = connection.getMetaData().getTables(null, null, tableName, null)) {
            while (resultSet.next()) {
                String existingTableName = resultSet.getString("TABLE_NAME");
                if (existingTableName != null && existingTableName.equalsIgnoreCase(tableName)) {
                    tableExists = true;
                    break;
                }
            }
        }
        return tableExists;
    }

    private void createTable(Connection connection, Class<?> clazz) throws SQLException {
        Table tableAnnotation = clazz.getAnnotation(Table.class);
        if (tableAnnotation == null) {
            throw new IllegalArgumentException("Класс не помечен аннотацией @Table");
        }
        String tableName = tableAnnotation.name();

        StringBuilder createTableQuery = new StringBuilder("CREATE TABLE ");
        createTableQuery.append(tableName).append(" (");

        Field[] fields = clazz.getDeclaredFields();
        boolean idFound = false;
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                Column columnAnnotation = field.getAnnotation(Column.class);
                if (field.isAnnotationPresent(Id.class)) {
                    createTableQuery.append(columnAnnotation.name()).append(" INT AUTO_INCREMENT PRIMARY KEY,");
                    idFound = true;
                } else {
                    createTableQuery.append(columnAnnotation.name()).append(" ").append(getColumnType(field)).append(",");
                }
            }
        }

        // Добавляем поле id в случае, если оно не было найдено в классе
        if (!idFound) {
            createTableQuery.append("id INT AUTO_INCREMENT PRIMARY KEY,");
        }

        createTableQuery.deleteCharAt(createTableQuery.length() - 1);
        createTableQuery.append(")");

        try (PreparedStatement preparedStatement = connection.prepareStatement(createTableQuery.toString())) {
            preparedStatement.executeUpdate();
        }
    }


    private String getColumnType(Field field) {
        Class<?> fieldType = field.getType();
        if (fieldType == int.class || fieldType == Integer.class) {
            return "INT";
        } else if (fieldType == String.class) {
            return "VARCHAR(255)";
        } else {
            throw new IllegalArgumentException("Unsupported field type: " + fieldType.getName());
        }
    }
}
