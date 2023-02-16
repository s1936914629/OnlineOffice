package cn.org.sqx.emos.wx.config.xss;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @auther: sqx
 * @Date: 2022-11-13
 */
@WebFilter(urlPatterns = "/*")
public class XssFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(req);
        chain.doFilter(wrapper,response);
    }

    @Override
    public void destroy() {

    }
}
