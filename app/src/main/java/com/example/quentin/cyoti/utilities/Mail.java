package com.example.quentin.cyoti.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import javax.mail.MessagingException;
import javax.mail.Multipart;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by Vincent on 06/06/2015.
 */
public class Mail extends javax.mail.Authenticator{
    private String senderName;
    private String senderPwd;
    private String senderMail;
    private String[] receiver;

    private String port;
    private String socketPort;

    private String host;

    private String subject;
    private String body;

    private boolean auth;

    private boolean debuggable;

    private Multipart multipart;

    public final static String NEW_CHALLENGE_SUBJECT = "You've been challenged !";
    public final static String NEW_FRIEND_SUBJECT = "Someone wants to be your friend !";
    public final static String NEW_EVIDENCE_SUBJECT = "An evidence has been posted for your challenge !";

    public Mail() {
        host = "smtp.gmail.com";
        port = "465";
        socketPort = "465";

        senderName = "contact.cyoti@gmail.com";
        senderMail = "contact.cyoti@gmail.com";
        senderPwd = "a-v-q-g-cyo!";

        subject = "";
        body = "";

        debuggable = false;
        auth = true;

        multipart = new MimeMultipart();

        // There is something wrong with MailCap, javamail can not find a handler for the multipart/mixed part, so this bit needs to be added.
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
    }

    public void setSubject(String subject) { this.subject = subject; }

    public void setBody(String body) { this.body = body; }

    public void setReceiver(String[] receiver) { this.receiver = receiver; }

    public boolean sendMail() throws Exception {
        Properties props = _setProperties();

        if(!senderName.equals("") && !senderPwd.equals("") && receiver.length > 0 &&
                !senderMail.equals("") && !subject.equals("") && !body.equals("")) {
            Session session = Session.getInstance(props, this);

            MimeMessage msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(senderMail));

            InternetAddress[] addressTo = new InternetAddress[receiver.length];
            for (int i = 0; i < receiver.length; i++) {
                addressTo[i] = new InternetAddress(receiver[i]);
            }
            msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);

            msg.setSubject(subject);
            msg.setSentDate(new Date());

            // setup message body
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);
            multipart.addBodyPart(messageBodyPart);

            // Put parts in message
            msg.setContent(multipart);

            // send email
            Transport.send(msg);

            return true;
        } else {
            return false;
        }
    }


    private Properties _setProperties() {
        Properties props = new Properties();

        props.put("mail.smtp.host", host);

        if(debuggable) {
            props.put("mail.debug", "true");
        }

        if(auth) {
            props.put("mail.smtp.auth", "true");
        }

        props.put("mail.smtp.port", port);
        props.put("mail.smtp.socketFactory.port", socketPort);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        return props;
    }

    public String getSenderMail() { return this.senderMail; }
    //public String getSenderName() { return this.senderName; }
    public String getSenderPwd() { return this.senderPwd; }


    public void sendNewChallengeAsyncMail(final Mail m, final ParseUser user, final ParseObject friend, final String description) {
        AsyncTask<Mail,Void,String> sendingTask = new AsyncTask<Mail, Void, String>() {

            @Override
            protected String doInBackground(Mail... mails) {
                int nbMails = mails.length;
                String result = null;

                for (int i=0; i<nbMails; i++) {
                    String[] receiver = {friend.get("email").toString()};
                    mails[i].setReceiver(receiver);
                    mails[i].setSubject(Mail.NEW_CHALLENGE_SUBJECT);
                    mails[i].setBody(user.get("username") + " challenged you to " + description
                                        + "\nWould you rise to this challenge ?");

                    try {
                        if(mails[i].sendMail()) {
                            result = "Mail OK !";
                        } else {
                            result = "Mail NOK !";
                        }
                    } catch(Exception e) {
                        Log.e("mail", "Could not send email", e);
                        result = "Mail exception !";
                    }

                }
                return result;
            }
        }.execute(m);
    }


    public void sendNewFriendAsyncMail(final Mail m, final ParseUser sender, final ParseObject friend) {
        AsyncTask<Mail,Void,String> sendingTask = new AsyncTask<Mail, Void, String>() {

            @Override
            protected String doInBackground(Mail... mails) {
                int nbMails = mails.length;
                String result = null;

                for (int i=0; i<nbMails; i++) {
                    String[] receiver = {friend.get("email").toString()};
                    mails[i].setReceiver(receiver);
                    mails[i].setSubject(Mail.NEW_FRIEND_SUBJECT);
                    mails[i].setBody(sender.get("username") + " wants to be your friend."
                                        + "\nGo on your profile and accept his request !");

                    try {
                        if(mails[i].sendMail()) {
                            result = "Mail OK !";
                        } else {
                            result = "Mail NOK !";
                        }
                    } catch(Exception e) {
                        Log.e("mail", "Could not send email", e);
                        result = "Mail exception !";
                    }

                }
                return result;
            }
        }.execute(m);
    }

    public void sendNewEvidenceAsyncMail(final Mail m, final ParseUser sender, final ParseObject friend) {
        AsyncTask<Mail,Void,String> sendingTask = new AsyncTask<Mail, Void, String>() {

            @Override
            protected String doInBackground(Mail... mails) {
                int nbMails = mails.length;
                String result = null;

                for (int i=0; i<nbMails; i++) {
                    String[] receiver = {friend.get("email").toString()};
                    mails[i].setReceiver(receiver);
                    mails[i].setSubject(Mail.NEW_EVIDENCE_SUBJECT);
                    mails[i].setBody(sender.get("username") + " posted an evidence for your challenge."
                            + "\nGo on the app to see it !");

                    try {
                        if(mails[i].sendMail()) {
                            result = "Mail OK !";
                        } else {
                            result = "Mail NOK !";
                        }
                    } catch(Exception e) {
                        Log.e("mail", "Could not send email", e);
                        result = "Mail exception !";
                    }

                }
                return result;
            }
        }.execute(m);
    }

    public boolean isMailingAllowed(ParseObject receiver) {
        ParseQuery userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("username", receiver.get("username"));

        try {
            ParseObject user = userQuery.getFirst();
            boolean mailingNotifications = user.getBoolean("newsletter");

            return mailingNotifications;
        } catch (ParseException e) {
            e.printStackTrace();

            return false;
        }
    }


    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(this.getSenderMail(), this.getSenderPwd());
    }
}
