package com.nika.annotations.entity;

import java.time.LocalDate;

public class Message {
    private String text;
    private LocalDate sendDate;

    public Message(String text, LocalDate sendDate) {
        this.text = text;
        this.sendDate = sendDate;
    }

    public String getText() {
        return text;
    }

    public LocalDate getSendDate() {
        return sendDate;
    }
}
