package run.mone.mcp.ipinfo.service;

import java.util.Map;
import java.util.HashMap;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.google.gson.Gson;

import io.ipinfo.api.IPinfo;
import io.ipinfo.api.errors.RateLimitedException;
import io.ipinfo.api.model.IPResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

@Slf4j
public class IpinfoService {
    public static Map<String, Object> getIpDetail(String token, String ip) {
        IPinfo ipInfo = new IPinfo.Builder()
                .setToken(token)
                .build();

        try {
            IPResponse response = ipInfo.lookupIP(ip);

            Map<String, Object> result = new HashMap<>();
            result.put("city", response.getCity());
            result.put("region", response.getRegion());
            result.put("country", response.getCountryName());
            result.put("latitude", response.getLatitude());
            result.put("longitude", response.getLongitude());
            result.put("timezone", response.getTimezone());

            return result;
        } catch (RateLimitedException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getClientIp() {
        String[] ipServices = {
            "https://api.ipify.org",
            "https://api.ip.sb/ip",
            "https://api.ipify.org/?format=text",
            "https://ip-api.com/line/?fields=query"
        };

        for (String service : ipServices) {
            try {
                URL url = new URL(service);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String ip = reader.readLine();
                    if (ip != null && !ip.trim().isEmpty()) {
                        return ip.trim();
                    }
                }
            } catch (Exception e) {
                log.debug("Failed to get IP from " + service + ": " + e.getMessage());
                continue;
            }
        }
        
        log.error("Failed to get client IP from all services");
        return null;
    }
}