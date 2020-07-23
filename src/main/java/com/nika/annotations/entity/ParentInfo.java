package com.nika.annotations.entity;

import javax.persistence.Embeddable;

@Embeddable
public class ParentInfo {
    private String firstName;
    private String lastName;
    private String phoneNumber;

    public ParentInfo(String firstName, String lastName, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
