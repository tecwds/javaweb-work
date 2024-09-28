import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(name = "BuyServlet", urlPatterns = "/buy")
public class BuyServlet extends HttpServlet {
    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html;charset=utf8");
        // 用户是否登陆

        // 从 cookie 中获取 SessionID
        Cookie[] cookies = req.getCookies();
        String sessionId = null;
        if (null == cookies) {
            res.getWriter().println("购买失败，你没有登陆");
            return;
        }
        for (Cookie cookie : cookies) {
            if ("JSESSIONID".equals(cookie.getName())) {
                sessionId = cookie.getValue();
                break;
            }
        }

        HttpSession se = req.getSession(false);
        System.out.println("Cookie 的 sessionId = " + sessionId);
        System.out.println("服务器的 session = " + (se == null ? "null" : se.getId()));

        // sessionID 不为空，且 sessionID 与当前 session 相等
        if (null != sessionId && req.getSession().getId().equals(sessionId)) {
            res.getWriter().println("成功购买，你花费了1000元");
            return;
        }

        res.getWriter().println("购买失败，你没有登陆");

    }
}
