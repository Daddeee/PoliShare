package it.polimi.polishare.server.utils;

import com.icegreen.greenmail.util.GreenMail;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MailerTest {
    private final GreenMail mailServer = new GreenMail();

    @BeforeEach
    public void setUp() {
        mailServer.start();

        final Properties mailSessionProperties = new Properties();
        mailSessionProperties.put("mail.smtp.port", String.valueOf(mailServer.getSmtp().getPort()));

        Mailer.setProps(mailSessionProperties);
        Mailer.setAuth(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return super.getPasswordAuthentication();
            }
        });
    }

    @Test
    public void testSendCredentials() throws MessagingException, IOException {
        String username = "provaUsername";
        String password = "provaPassword";
        String email = "prova@mail.com";
        String expectedSubject = "Registrazione a Polishare";

        Mailer.sendCredentials(username, password, email);
        MimeMessage[] messages = mailServer.getReceivedMessages();

        assertEquals(1, messages.length, "Failed to receive registration email");
        assertEquals(1, messages[0].getHeader("To").length, "Malformed To headers in received registration email");
        assertEquals(email, messages[0].getHeader("To")[0]);

        assertEquals(expectedSubject, messages[0].getSubject(), "Malformed subject in received registration email");
    }
}