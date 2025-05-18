package com.exemplo.servlet;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.util.Base64;

public class JwtUtil {
    private static final String SECRET = "secreto123";

    public static String generateToken(String username) {
        String header = 
Base64.getEncoder().encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());
        String payload = Base64.getEncoder().encodeToString((
            "{\"sub\":\"" + username + "\",\"exp\":" + 
(System.currentTimeMillis() + 3600_000) + "}"
        ).getBytes());

        String signature = hmacSha256(header + "." + payload, SECRET);
        return header + "." + payload + "." + signature;
    }

    public static boolean validateToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) return false;

        String signature = hmacSha256(parts[0] + "." + parts[1], SECRET);
        if (!signature.equals(parts[2])) return false;

        String payloadJson = new 
String(Base64.getDecoder().decode(parts[1]));
        JsonObject payload = Json.createReader(new
                StringReader(payloadJson)).readObject();
        long exp = payload.getJsonNumber("exp").longValue();

        return exp > System.currentTimeMillis();
    }

    private static String hmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
            return 
Base64.getEncoder().encodeToString(mac.doFinal(data.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}

