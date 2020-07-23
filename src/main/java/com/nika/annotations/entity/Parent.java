package com.nika.annotations.entity;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Parent {

    @Embedded
    private ParentInfo parentInfo;

    public Parent() { }

    public Parent(ParentInfo parentInfo) {
        this.parentInfo = parentInfo;
    }

    @OneToMany(mappedBy = "parent")
    private List<Child> children;

    public void doSomething(Child child, LocalDate date) {
        System.out.println("aaa");
    }
}
