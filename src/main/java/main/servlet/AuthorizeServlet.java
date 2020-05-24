package main.servlet;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthorizeServlet extends HttpServlet {
    private ConcurrentHashMap<String, Integer> activeSessions;

    public AuthorizeServlet() {
        this.activeSessions = new ConcurrentHashMap<>();
    }

    public HttpSession getSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(true);
    }

    public boolean isUserAuthorize() {
        String sessionId = getSession().getId();
        return activeSessions.containsKey(sessionId);
    }

    public int getAuthorizedUserId() {
        String sessionId = getSession().getId();
        return activeSessions.get(sessionId);
    }

    public void authorizeUser(int userId) {
        String sessionId = getSession().getId();
        activeSessions.put(sessionId, userId);
    }
}
