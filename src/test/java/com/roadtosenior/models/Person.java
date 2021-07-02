package com.roadtosenior.models;

import com.roadtosenior.annotations.Column;
import com.roadtosenior.annotations.PK;
import com.roadtosenior.annotations.Table;

@Table(name = "persons")
public class Person {
    @PK
    @Column
    private int id;

    @Column(name = "person_name")
    private String name;

    @Column
    private double salary;

    public Person(int id, String name, double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
}
