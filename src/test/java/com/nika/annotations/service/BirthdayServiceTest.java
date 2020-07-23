package com.nika.annotations.service;

import com.nika.annotations.entity.User;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

import static java.util.Arrays.asList;

public class BirthdayServiceTest {
    public static final LocalDate TODAY = LocalDate.parse("2020-06-20");

    @Test
    public void shouldSendBirthdayMessagesOnlyToUsersWithBirthDay() throws Exception {
        User userWithBirthday = new User("nika", "jamburia", LocalDate.parse("2005-06-20"));
        User userWithoutBirthday = new User("nika", "jamburia", LocalDate.parse("2005-07-20"));

        BirthdayService birthdayService = new BirthdayService();
        birthdayService.setMessageService(new MessageService());

        birthdayService.sendBirthdayMessages(asList(userWithBirthday, userWithoutBirthday), TODAY);

        Assert.assertEquals(0, userWithoutBirthday.getMessages().size());
        Assert.assertEquals("Happy Birthday nika!", userWithBirthday.getMessages().get(0).getText());

    }
}
