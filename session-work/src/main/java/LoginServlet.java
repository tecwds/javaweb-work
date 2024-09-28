import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf8");

        HttpSession session = req.getSession();

        String username = req.getParameter("username");

        if (Objects.nonNull(session.getAttribute(username))) {
            // 已经登陆过了
            System.out.println("用户" + username + "已经登陆");
            resp.getWriter().println("用户" + username + "已经登陆");
            resp.getWriter().println("SessionID");

            for (Cookie cookie : req.getCookies()) {
                resp.getWriter().println(cookie.getName() + ":" + cookie.getValue());
            }

            return;
        }

        // 没有登陆

        // 假定账号密码为: admin, 123456
        String password = req.getParameter("password");
        if ("admin".equals(username) && "123456".equals(password)) {
            // 登陆成功
            session.setAttribute(username, username);

            // 设置 Cookie
            Cookie sessionCookie = new Cookie("JSESSIONID", session.getId());

            // 防御 XSS
            sessionCookie.setHttpOnly(true);

            // 通过 HTTPS 发送
            // sessionCookie.setSecure(true);

            resp.addCookie(sessionCookie);

            // 设置会话超时时间
            session.setMaxInactiveInterval(30 * 60); // 30min

            resp.getWriter().println("登陆成功");

            for (Cookie cookie : req.getCookies()) {
                resp.getWriter().println(cookie.getName() + ":" + cookie.getValue());
            }

        } else {
            // 登陆失败
            resp.getWriter().println("登陆失败");
        }
    }
}
