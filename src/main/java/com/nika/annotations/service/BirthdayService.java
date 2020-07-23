package com.nika.annotations.service;


import com.nika.annotations.entity.User;
import com.nika.annotations.framework.annotation.Service;

import java.time.LocalDate;
import java.util.List;

@Service(name = "birthday")
public class BirthdayService {
    // TODO: Figure out how to inject fields from context to lazy services
    @Service(name = "message")
    private MessageService messageService;

    public void sendBirthdayMessages(List<User> users, LocalDate date) {
        users.stream()
            .filter(user -> hasBirthday(user, date))
            .forEach(user ->
                    messageService.sendMessage("Happy Birthday " + user.getFirstName() + "!", date, user));
    }

    private boolean hasBirthday(User user, LocalDate date) {
        return user.getBirthDate().getMonthValue() == date.getMonthValue()
                && user.getBirthDate().getDayOfMonth() == date.getDayOfMonth();
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public MessageService getMessageService() {
        return messageService;
    }
}
