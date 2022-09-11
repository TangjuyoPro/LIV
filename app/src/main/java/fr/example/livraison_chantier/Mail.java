package fr.example.livraison_chantier;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import android.util.Log;

/**
 * Classe qui permet l'envoie d'un mail automatique
 * (ATTENTION CETTE CLASSE N'EST PLUS UTILE DANS LES DERNIERES VERSIONS)
 */
@Deprecated
public class Mail {

    final String emailPort = "587";// gmail's smtp port
    final String smtpAuth = "true";
    final String starttls = "true";
    final String emailHost = "";//gmail smtp


    String fromEmail;
    String fromPassword;
    List toEmailList;
    String emailSubject;
    String emailBody;

    Properties emailProperties;
    Session mailSession;
    MimeMessage emailMessage;

    /**
     *
     */
    public Mail() {
    }

    /**
     * Renseigne toutes les informations nécessaire à la création d'une connexion de mailing
     * @param fromEmail l'email du destinataire
     * @param fromPassword le mot de passe du destinataire
     * @param toEmailList une liste des email de destinations
     * @param emailSubject l'objet du mail
     * @param emailBody le corps du mail
     */
    public Mail(String fromEmail, String fromPassword,
                 List toEmailList, String emailSubject, String emailBody) {
        this.fromEmail = fromEmail;
        this.fromPassword = fromPassword;
        this.toEmailList = toEmailList;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;

        emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", emailPort);
        emailProperties.put("mail.smtp.auth", smtpAuth);
        emailProperties.put("mail.smtp.starttls.enable", starttls);
        Log.i("GMail", "Mail server properties set.");
    }

    /**
     * Permet de créer le mail
     * @return renvoie un OBJECT mail
     * @throws AddressException
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    public MimeMessage createEmailMessage() throws AddressException,MessagingException, UnsupportedEncodingException {

        mailSession = Session.getDefaultInstance(emailProperties, null);
        emailMessage = new MimeMessage(mailSession);

        emailMessage.setFrom(new InternetAddress(fromEmail, fromEmail));
        for(Object tomail : toEmailList) {
            Log.i("GMail","toEmail: "+(String) tomail);
            emailMessage.addRecipient(Message.RecipientType.TO,
                    new InternetAddress((String) tomail));
        }

        emailMessage.setSubject(emailSubject);
        //emailMessage.setContent(emailBody, "text/html");// for a html email
        //emailMessage.setContent(emailBody, "text/rtf");// for a rtf email ( met en PJ un .text )
        emailMessage.setContent(emailBody, "text/csv");// for a csv email ( met en PJ un .text )
        emailMessage.setFileName("data.csv");
        //emailMessage.setText(emailBody);// for a text email
        Log.i("GMail", "Email Message created.");
        return emailMessage;
    }

    /**
     * Permet d'envoyer le mail
     * @throws AddressException
     * @throws MessagingException
     */
    public void sendEmail() throws AddressException, MessagingException {

        Transport transport = mailSession.getTransport("smtp");
        transport.connect(emailHost, fromEmail, fromPassword);
        Log.i("GMail","allrecipients: "+emailMessage.getAllRecipients());
        transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
        transport.close();
        Log.i("GMail", "Email sent successfully.");
    }

}