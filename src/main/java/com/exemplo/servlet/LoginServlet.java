package com.exemplo.servlet;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.stream.Collectors;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse
resp)
        throws IOException {

        String body = new BufferedReader(new
                InputStreamReader(req.getInputStream()))
                .lines().collect(Collectors.joining());

        JsonObject json = Json.createReader(new
                StringReader(body)).readObject();
        String username = json.getString("username");
        String password = json.getString("password");

        if ("admin".equals(username) && "123".equals(password)) {
            String token = JwtUtil.generateToken(username);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"token\": \"" + token + "\"}");
        } else {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        resp.getWriter().write("Use POST com JSON {username, password}");
    }

}

