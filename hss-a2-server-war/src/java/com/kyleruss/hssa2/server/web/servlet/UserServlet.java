//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.web.servlet;

import com.kyleruss.hssa2.commons.Password;
import com.kyleruss.hssa2.commons.RequestPaths;
import com.kyleruss.hssa2.server.web.app.CryptoController;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "UserServlet", urlPatterns = 
{
    RequestPaths.SERV_CONNECT_REQ,
    RequestPaths.PASS_REQ,
    RequestPaths.USER_LIST_REQ,
    RequestPaths.PROFILE_UP_REQ,
    RequestPaths.SERV_DISCON_REQ
})
public class UserServlet extends HttpServlet 
{
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException 
    {
        String password =   "abcdefghrlemfyir";
        String salt     =   "welymcturkcmpoei";
        
        try
        {
            String plaintext = "Hello World";
            String ciphertext = CryptoController.getInstance().pbeEncrypt(new Password(password), salt, plaintext);
            String dectext = CryptoController.getInstance().pbeDecrypt(new Password(password), salt, ciphertext);
            
            System.out.println("cipher text: " + ciphertext);
            System.out.println("plain text: " + dectext);
        }
        
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException 
    {
        String path =   request.getServletPath();
        
        switch(path)
        {
            case RequestPaths.SERV_CONNECT_REQ:
            case RequestPaths.PASS_REQ:
            case RequestPaths.USER_LIST_REQ:
            case RequestPaths.PROFILE_UP_REQ:
            case RequestPaths.SERV_DISCON_REQ:
        }
    }
    
    protected void processUserConnect(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException 
    {
        
    }
    
    protected void processUserDisconnect(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException 
    {
        
    }
}
