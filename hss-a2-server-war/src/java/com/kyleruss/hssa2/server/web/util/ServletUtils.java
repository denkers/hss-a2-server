//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.web.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kyleruss.hssa2.commons.EncryptedSession;
import com.kyleruss.hssa2.server.web.app.ServerKeyManager;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;

public class ServletUtils 
{
    
    public static String getJsonResponseString(Object responseData)
    {
        if(responseData instanceof JsonObject)
            return ((JsonObject) responseData).toString();
        
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
    
    public static JsonObject prepareKeySessionResponse(EncryptedSession enc) 
    throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException
    {
        enc.initCipher(Cipher.ENCRYPT_MODE);
        String data             =   Base64.getEncoder().encodeToString(enc.processData());
        String key              =   Base64.getEncoder().encodeToString(enc.encryptKey());
        JsonObject responseObj  =   new JsonObject();
        
        responseObj.addProperty("key", key);
        responseObj.addProperty("data", data);
        return responseObj;
    }
    
    public static JsonObject parseJsonInput(String json)
    {
        return new JsonParser().parse(json).getAsJsonObject();
    }
    
    public static String getClientJson(HttpServletRequest request)
    {
        return request.getParameter("clientData");
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
