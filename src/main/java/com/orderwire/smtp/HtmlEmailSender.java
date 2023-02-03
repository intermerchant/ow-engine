package com.orderwire.smtp;

import com.orderwire.logging.UtilLogger;

import jakarta.mail.*;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

public class HtmlEmailSender {
    private final String owThreadName;
    private final HashMap emailDetailMap;

    public HtmlEmailSender(HashMap _emailDetailsMap){
        owThreadName = _emailDetailsMap.get("owThreadName").toString();
        emailDetailMap = _emailDetailsMap;
    }

    public boolean sendHtmlEmail() {

        Boolean procStatus = true;

        try {
            String toAddress = String.valueOf(emailDetailMap.get("email_to"));
            String toCCAddress = String.valueOf(emailDetailMap.get("email_cc"));
            String toBCCAddress = String.valueOf(emailDetailMap.get("email_bcc"));
            String email_subject = String.valueOf(emailDetailMap.get("email_subject"));
            String email_message = String.valueOf(emailDetailMap.get("email_message"));

            // sets SMTP server properties
            Properties properties = new Properties();
            properties.put("mail.smtp.host", "mail.infomeld.com");
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.ssl.enable", "true");
            String username = "support@infomeld.com";
            String password = "0T=-ozqE4y";

            // creates a new session with an authenticator
            Authenticator auth = new Authenticator() {
                public PasswordAuthentication getPasswordAuthentication() { return new PasswordAuthentication(username, password); }
            };
            Session session = Session.getInstance(properties, auth);

            // creates a new e-mail message
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(username));
            Address[] replyAddress = InternetAddress.parse(username);
            msg.setReplyTo(replyAddress);

            if (toAddress.length() > 1) {
                msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
            }
            if (toCCAddress.length() > 1){
                msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse(toCCAddress));
            }
            if (toBCCAddress.length() > 1) {
                msg.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(toBCCAddress));
            }

            msg.setSubject(email_subject);
            msg.setSentDate(new Date());
            msg.setContent(email_message, "text/html");

            Transport.send(msg);

        } catch (AddressException ae) {
            procStatus = false;
            UtilLogger.setDbStatus(owThreadName, "HtmlEmailSender", "sendHtmlEmail", "", "Address Exception", ae.getMessage());
        } catch (MessagingException me) {
            procStatus = false;
            UtilLogger.setDbStatus(owThreadName, "HtmlEmailSender", "sendHtmlEmail", "", "Messaging Exception", me.getMessage());
        } catch (Exception exce){
            procStatus = false;
            UtilLogger.setDbStatus(owThreadName, "HtmlEmailSender", "sendHtmlEmail", "", "Exception", exce.getMessage());
        }

        return procStatus;
    }

}
