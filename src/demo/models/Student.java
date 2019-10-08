package demo.models;

import annotations.Column;
import annotations.ManyToMany;
import annotations.Model;
import annotations.PrimaryKey;

import java.util.Objects;
import java.util.Set;

@Model(tableName = "student", primaryKey = "id")
public class Student {

    @PrimaryKey
    private int id;

    @Column(fieldName = "name")
    private String name;

    @Column(fieldName = "surname")
    private String surname;

    @Column(fieldName = "average_mark")
    private double averageMark;

    @ManyToMany(table = "teacher")
    private Set<Teacher> teachers;

    public Set<Teacher> getTeachers() {
        return teachers;
    }

    public Student() {
    }

    public Student(String name, String surname, double averageMark) {
        this.name = name;
        this.surname = surname;
        this.averageMark = averageMark;
    }

    public Student(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public Student(int id, String name, String surname, double averageMark) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.averageMark = averageMark;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return id == student.id &&
                Double.compare(student.averageMark, averageMark) == 0 &&
                name.equals(student.name) &&
                surname.equals(student.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, averageMark);
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", averageMark=" + averageMark +
                '}';
    }
}
