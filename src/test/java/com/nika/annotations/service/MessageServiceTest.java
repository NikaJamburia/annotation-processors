package com.nika.annotations.service;

import com.nika.annotations.entity.User;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

public class MessageServiceTest {
    @Test
    public void shouldCorrectlySendMessage() throws Exception {
        User user = new User("nika", "jamburia", LocalDate.parse("1998-12-24"));
        MessageService messageService = new MessageService();

        messageService.sendMessage("zdarova", LocalDate.now(), user);

        Assert.assertEquals(1, user.getMessages().size());
        Assert.assertEquals("zdarova", user.getMessages().get(0).getText());
    }
}
