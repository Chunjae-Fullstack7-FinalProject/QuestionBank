package net.questionbank.config;

import net.questionbank.filter.LoginCheckFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<LoginCheckFilter> loginCheckFilter() {
        FilterRegistrationBean<LoginCheckFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LoginCheckFilter());
        filterRegistrationBean.addUrlPatterns(
                "/member/logout"
                ,"/reports/*"
                ,"/customExam/*"
                ,"/bridge/*"
                ,"/api/storage/*"
                ,"/storage/*"
                ,"/api/customExam/*"
                ,"/tests/*"
        );
        return filterRegistrationBean;
    }
}
