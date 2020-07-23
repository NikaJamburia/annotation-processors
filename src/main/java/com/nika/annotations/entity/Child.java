package com.nika.annotations.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Child {
    @ManyToOne
    private Parent parent;
}
