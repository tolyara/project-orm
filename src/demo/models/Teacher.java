package demo.models;

import annotations.Column;
import annotations.ManyToMany;
import annotations.Model;
import annotations.PrimaryKey;

import java.util.Objects;
import java.util.Set;

@Model(tableName = "teacher", primaryKey = "id")
public class Teacher {

    @PrimaryKey
    private int id;

    @Column(fieldName = "name")
    private String name;

    @Column(fieldName = "surname")
    private String surname;

    @Column(fieldName = "sex")
    private boolean sex;

    @ManyToMany(table = "student")
    private Set<Student> students;

    public Set<Student> getStudents() {
        return students;
    }

    public Teacher() {
    }

    public Teacher(String name, String surname, boolean sex) {
        this.name = name;
        this.surname = surname;
        this.sex = sex;
    }

    public Teacher(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public Teacher(int id, String name, String surname, boolean sex) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.sex = sex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teacher teacher = (Teacher) o;
        return id == teacher.id &&
                sex == teacher.sex &&
                name.equals(teacher.name) &&
                surname.equals(teacher.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, sex);
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", sex=" + sex +
                '}';
    }
}
