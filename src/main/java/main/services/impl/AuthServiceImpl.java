package main.services.impl;

import main.services.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentHashMap;

//@Component
@Service
public class AuthServiceImpl extends HttpServlet implements AuthService {
    private ConcurrentHashMap<String, Long> activeSessions = new ConcurrentHashMap<>();

//    public AuthServiceImpl() {
//        this.activeSessions = new ConcurrentHashMap<>();
//    }

    private HttpSession getSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(true);
    }

    public boolean isUserAuthorize() {
        String sessionId = getSession().getId();
        return activeSessions.containsKey(sessionId);
    }

    public long getAuthorizedUserId() {
        String sessionId = getSession().getId();
        return activeSessions.get(sessionId);
    }

    public void authorizeUser(long userId) {
        String sessionId = getSession().getId();
        activeSessions.put(sessionId, userId);
    }

    public void removeAuthorizedUser() {
        String sessionId = getSession().getId();
        activeSessions.remove(sessionId);
    }
}
