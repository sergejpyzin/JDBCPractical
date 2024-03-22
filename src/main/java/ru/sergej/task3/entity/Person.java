package ru.sergej.task3.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sergej.task3.annotations.Column;
import ru.sergej.task3.annotations.Id;
import ru.sergej.task3.annotations.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "persons")
public class Person {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "age")
    private int age;

    public Person(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    @Override
    public String toString() {
        return String.format("Персона: id = %d\nИмя: %s, Фамилия: %s, Возраст: %d\n", id, firstName, lastName, age);
    }
}
