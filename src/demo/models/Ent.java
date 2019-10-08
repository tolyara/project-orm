package demo.models;

import annotations.*;
import storages.Entity;

@Model(tableName = "entity", primaryKey = "id")
public class Ent {

    @PrimaryKey
    private int id;

    @Column(fieldName = "name")
    private String name;

    //@OneToOne(table = "worker", column = "id")
    Worker worker;

    public Ent(){}
    public Ent(String name){
        this.name = name;
    }

}
