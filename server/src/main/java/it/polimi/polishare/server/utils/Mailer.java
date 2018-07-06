package it.polimi.polishare.server.utils;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class Mailer {
    private static final String myMail = "polishare@outlook.com";
    private static final String myPass = "s3cur3p4ss";

    private static Properties props = new Properties();
    private static Authenticator auth = new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(myMail, myPass);
        }
    };

    static {
        props.put("mail.smtp.host", "smtp-mail.outlook.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.auth", "true");
    }

    public static void setProps(Properties props) {
        Mailer.props = props;
    }

    public static void setAuth(Authenticator auth) {
        Mailer.auth = auth;
    }

    public static void sendCredentials(String username, String password, String mail) throws MessagingException {
        Session session = Session.getInstance(props, auth);
        MimeMessage msg = new MimeMessage(session);

        msg.setText(
                "Congratulazioni,\n\n" +
                "la sua registrazione a PoliShare è avvenuta con successo. Le credenziali di accesso sono:\n\n" +
                "\tUsername: " + username + "\n" +
                "\tPassword: " + password + "\n\n" +
                "Cordiali saluti.");
        msg.setSubject("Registrazione a Polishare");
        msg.setFrom(new InternetAddress(myMail));
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(mail));
        Transport.send(msg);
    }
}
