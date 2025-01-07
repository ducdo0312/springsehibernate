package com.example.springsehibernate.Config;

import com.example.springsehibernate.Entity.AcademicYearUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // Lấy session từ request
        HttpSession session = request.getSession();

        // Đặt năm học hiện tại vào session (hoặc bạn có thể thực hiện bất kỳ hành động nào bạn muốn tại đây)
        String currentAcademicYear = AcademicYearUtil.getCurrentAcademicYear(); // Code để lấy năm học hiện tại
        session.setAttribute("currentAcademicYear", currentAcademicYear);

        // Chuyển hướng đến trang `/home`
        response.sendRedirect("/home");
    }
}