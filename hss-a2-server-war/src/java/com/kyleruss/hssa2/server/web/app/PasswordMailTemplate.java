//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.web.app;

public class PasswordMailTemplate 
{
    private static final String paramName   =   "{PASSWORD_PLACEHOLDER}";
    private static String template = 
    "<html>"
    + "<body>"
        + "<h1>Hello,</h1>"
        + "<h3><small>Below is your vefification password, please enter the code to finalize your registration</small></h3>"
        + "<div style='border: 1px solid black; padding: 15px'>" + paramName + "</div>"
    + "</body>"
    + "</html>";
    
    public String getTemplate(String password)
    {
        String injectedTemplate =   template.replace(paramName, password);
        return injectedTemplate;
    }
}
