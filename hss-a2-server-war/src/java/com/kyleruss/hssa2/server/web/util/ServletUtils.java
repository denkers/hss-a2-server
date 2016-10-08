//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.web.util;

import com.google.gson.Gson;
import java.io.IOException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

public class ServletUtils 
{
    
    public static String getJsonResponseString(Object responseData)
    {
        Gson gson           =   new Gson();
        String jsonResponse =   responseData == null? "" : gson.toJson(responseData);
        return jsonResponse;
    }
    
    
    public static void jsonResponse(HttpServletResponse response, Object responseData) 
    throws ServletException, IOException
    {
        String jsonResponse =   getJsonResponseString(responseData);
        response.setContentType("application/json");        
        response.getWriter().write(jsonResponse);
    }
    
    public static void encryptedJsonResponse(HttpServletResponse response, Object responseData, Cipher cipher) 
    throws ServletException, IOException, IllegalBlockSizeException, BadPaddingException
    {
        String jsonResponse     =   getJsonResponseString(responseData);
        byte[] cipherText       =   cipher.doFinal(jsonResponse.getBytes());
        String encodedResponse  =   Base64.getEncoder().encodeToString(cipherText);
        response.setContentType("text/plain");
        response.getWriter().write(encodedResponse);
    }
}
