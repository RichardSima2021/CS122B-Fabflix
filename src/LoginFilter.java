import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {
    private final ArrayList<String> allowedURIs = new ArrayList<>();

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

//        System.out.println("LoginFilter: " + httpRequest.getRequestURI());


        // Check if this URL is allowed to access without logging in
        if (this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
//            System.out.println("Allowed without login: " + httpRequest.getRequestURI());
            // Keep default action: pass along the filter chain
            chain.doFilter(request, response);
            return;
        }

        // Redirect to login page if the "user" attribute doesn't exist in session
        if (httpRequest.getSession().getAttribute("user") == null && httpRequest.getSession().getAttribute("employee") == null) {
//            System.out.println("User is null for request URI: " + httpRequest.getRequestURI());
//            System.out.println("Redirecting to login.html");

//            if(httpRequest.)
//            System.out.println(((HttpServletRequest) request).getRequestURI());
//            System.out.println(((HttpServletRequest) request).getPathInfo());
//            System.out.println(((HttpServletRequest) request).getContextPath());
            httpResponse.sendRedirect(((HttpServletRequest) request).getContextPath()+"/login.html");
//            chain.doFilter(request, response);
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
        allowedURIs.add("api/login");
        allowedURIs.add("login.html");
        allowedURIs.add("login.js");
        allowedURIs.add("api/dashboard_login");
    }

    public void destroy() {
        // ignored.
    }

}