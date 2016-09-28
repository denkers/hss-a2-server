//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.web.app;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MailController 
{
    private static MailController instance;
    private Properties smtpProperties;
    
    private MailController() 
    {
        initProperties();
    }
    
    private void initProperties()
    {
        smtpProperties   =   new Properties();
        smtpProperties.put("mail.smtp.auth", "true");
        smtpProperties.put("mail.smtp.starttls.enable", "true");
        smtpProperties.put("mail.smtp.host", Config.SMTP_HOST);
        smtpProperties.put("mail.smtp.port", Config.SMTP_PORT);
    }
    
    public void sendMail(String recvEmail, String content)
    {
        
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
