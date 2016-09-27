//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.web.util;

import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

public class ServletUtils 
{
    public static void jsonResponse(HttpServletResponse response, Object responseData) throws ServletException, IOException
    {
        Gson gson           =   new Gson();
        String jsonResponse =   responseData == null? "" : gson.toJson(responseData);
        response.setContentType("application/json");        
        response.getWriter().write(jsonResponse);
    }
}
