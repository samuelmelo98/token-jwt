package com.exemplo.servlet;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/api/protegido/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String ip = req.getRemoteAddr();
        String realIp = req.getHeader("X-Forwarded-For");
        String host = req.getRemoteHost();
        String port = String.valueOf(req.getRemotePort());
        String origin = req.getHeader("Origin");
        String referer = req.getHeader("Referer");
        String userAgent = req.getHeader("User-Agent");

        // 🔓 Libera CORS
        res.setHeader("Access-Control-Allow-Origin", "*"); // ou "null" para file://
        res.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
        res.setHeader("Access-Control-Allow-Headers", "chave-Authorization, Content-Type");//personaliza o header
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        res.setHeader("Access-Control-Max-Age", "86400");

        // ✅ Pré-flight (OPTIONS) deve responder direto
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // 🔐 Verificação do token JWT
        String auth = req.getHeader("chave-Authorization");

        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            if (JwtUtil.validateToken(token)) {
                chain.doFilter(request, response);
                return;
            }
        }

        res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}
