import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(filterName = "DashboardFilter", urlPatterns = "/_dashboard")
public class DashboardFilter implements Filter {
    private final ArrayList<String> allowedURIs = new ArrayList<>();

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        System.out.println("DashboardFilter: " + httpRequest.getRequestURI());


        // Check if this URL is allowed to access without logging in
        if (this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
            System.out.println("Allowed without login: " + httpRequest.getRequestURI());
            // Keep default action: pass along the filter chain
            chain.doFilter(request, response);
            return;
        }

        // Redirect to login page if the "employee" type doesn't exist in session
        if (httpRequest.getSession().getAttribute("accountType") == null || !httpRequest.getSession().getAttribute("accountType").equals("employee")) {
            httpResponse.sendRedirect(((HttpServletRequest) request).getContextPath()+"/_dashboard/login.html");
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean isUrlAllowedWithoutLogin(String requestURI) {
//        System.out.println("Attempting to access " + requestURI);
        /*
         Setup your own rules here to allow accessing some resources without logging in
         Always allow your own login related requests(html, js, servlet, etc..)
         You might also want to allow some CSS files, etc..
         */
        return allowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
    }

    public void init(FilterConfig fConfig) {
        allowedURIs.add("login.html");
        allowedURIs.add("login.js");
        allowedURIs.add("style.css");
//        allowedURIs.add("api/login");
//        allowedURIs.add("login.html");
//        allowedURIs.add("login.js");
        allowedURIs.add("api/dashboard_login");
    }

    public void destroy() {
        // ignored.
    }

}