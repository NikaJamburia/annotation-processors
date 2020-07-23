package com.nika.annotations.service;

import com.nika.annotations.entity.Message;
import com.nika.annotations.entity.User;
import com.nika.annotations.framework.annotation.Lazy;
import com.nika.annotations.framework.annotation.Service;

import java.time.LocalDate;
import java.util.List;

@Lazy
@Service(name = "message")
public class MessageService {
    public void sendMessage(String text, LocalDate date, User user) {
        user.sendMessage(new Message(text, date));
    }

    public List<Message> readMessagesFrom(User user) {
        return user.getMessages();
    }
}
