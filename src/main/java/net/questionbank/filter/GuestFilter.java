package net.questionbank.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import net.questionbank.annotation.Logging;
import net.questionbank.dto.MemberLoginDTO;
import net.questionbank.util.JSFunc;

import java.io.IOException;

@Log4j2
@Logging
public class GuestFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
        MemberLoginDTO loginDto = (MemberLoginDTO) session.getAttribute("loginDto");
        if (loginDto != null) {
            JSFunc.alertBack("잘못된 접근입니다.",response);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
