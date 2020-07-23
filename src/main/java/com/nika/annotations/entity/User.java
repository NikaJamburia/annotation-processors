package com.nika.annotations.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private List<Message> messages;

    public User(String firstName, String lastName, LocalDate birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.messages = new ArrayList<>();
    }

    public void sendMessage(Message message) {
        this.messages.add(message);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
