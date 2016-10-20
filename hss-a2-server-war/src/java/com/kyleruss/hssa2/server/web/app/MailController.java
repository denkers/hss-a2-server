//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.web.app;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailController 
{
    private static MailController instance;
    private Properties smtpProperties;
    
    private MailController() 
    {
        initProperties();
    }
    
    //Initializes the SMTP properties
    //See Config for SMTP configs
    private void initProperties()
    {
        smtpProperties   =   new Properties();
        smtpProperties.put("mail.smtp.starttls.enable", "true");
        smtpProperties.put("mail.smtp.auth", "true");
        smtpProperties.put("mail.smtp.host", Config.SMTP_HOST);
        smtpProperties.put("mail.smtp.port", Config.SMTP_PORT);
    }
    
    //Sends a email message to a recipient
    //recvEmail: the recipients email
    //content: the email/message body
    //subject: email subject
    //contentType: the message body/content type including charsset
    public void sendMail(String recvEmail, String content, String subject, String contentType) throws MessagingException
    {
        Session session     =   Session.getInstance(smtpProperties, new PassAuthenticator());
        MimeMessage msg     =   new MimeMessage(session);
        msg.setFrom(new InternetAddress(Config.SMTP_ACC));
        msg.setRecipient(RecipientType.TO, new InternetAddress(recvEmail));
        msg.setSubject(subject);
        msg.setContent(content, contentType);
        Transport.send(msg);
    }
    
    //Sends an email to the passed email address (@recvMail) with the generated code
    //Content is generated from the template in app.PasswordMailTemplate
    public void sendPasswordMail(String recvMail, String password) throws MessagingException
    {
        String subject      =   "SafeSMS Password Verification";
        String contentType  =   "text/html; charset=utf-8";   
        String content      =   PasswordMailTemplate.getTemplate(password);
        sendMail(recvMail, content, subject, contentType);
    }
    
    public static MailController getInstance()
    {
        if(instance == null) instance = new MailController();
        return instance;
    }
    
    private class PassAuthenticator extends Authenticator
    {
        @Override
        protected PasswordAuthentication getPasswordAuthentication()
        {
            return new PasswordAuthentication(Config.SMTP_ACC, Config.SMTP_ACC_PASS);
        }
    }
}
