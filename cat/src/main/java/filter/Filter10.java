package filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

@WebFilter(filterName = "Filter 1",
        urlPatterns = {"/*"}
)
public class Filter10 implements Filter {
    public void destroy(){}
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws ServletException, IOException{
        System.out.println("Filter 1 begins");
        HttpServletRequest request = (HttpServletRequest)req;
        String uri = request.getRequestURI();
        //打印url和执行时间
        System.out.println(uri +" @ "+new Date());
        chain.doFilter(req,resp);
        System.out.println("Filter 1 ends");
    }

}
