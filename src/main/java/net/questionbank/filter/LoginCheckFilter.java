package net.questionbank.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import net.questionbank.dto.MemberLoginDTO;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Log4j2
public class LoginCheckFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
        MemberLoginDTO loginDto = (MemberLoginDTO) session.getAttribute("loginDto");
        log.info(loginDto);
        if (loginDto == null) {
            String reqUri = request.getRequestURI();
            log.info("referer: {}", reqUri);
            log.info("subjectId : {}", request.getParameter("subjectId"));
            if(reqUri != null && reqUri.contains("/customExam")){
                session.setAttribute("goCustomExam",true);
                session.setAttribute("subjectId",request.getParameter("subjectId"));
            }
            response.sendRedirect("/member/login");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
