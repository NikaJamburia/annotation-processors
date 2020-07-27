package com.nika.annotations.framework.context;

import com.nika.annotations.entity.User;
import com.nika.annotations.service.BirthdayService;
import com.nika.annotations.service.MessageService;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;

public class ServiceContextTest {
    private ServiceContext context;
    private User user = new User("nika", "jamburia", LocalDate.parse("1998-12-24"));

    @Before
    public void initContext() {
        context = new ServiceContext(
                "com.nika.annotations.service.MessageService",
                "com.nika.annotations.service.BirthdayService"
        );
    }

    @Test
    public void shouldCorrectlyCreateServices() throws Exception {
        MessageService messageService = context.getService("message");
        BirthdayService birthdayService = context.getService("birthday");

        assert (messageService != null);
        assert (birthdayService != null);
        assert (birthdayService.getMessageService() == messageService);

        messageService.sendMessage("aaa", LocalDate.now(), user);
        assert (messageService.readMessagesFrom(user).size() == 1);
        System.out.println(messageService);

        birthdayService.sendBirthdayMessages(Arrays.asList(user), LocalDate.now());
    }
}
