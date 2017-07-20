package io.klib.example.osgi.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

@Component(property = {
                        HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN + "=/test",
                        HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_INIT_PARAM_PREFIX + "servletParam=paramValue"
}, service = Servlet.class)
public class SampleServlet extends HttpServlet {
    private static final long serialVersionUID = 7194000229063986746L;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter writer = response.getWriter();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        writer.write("<h1># Servlet Debug " + sdf.format(new Date()) + "</h1>");

        writer.write("# ServletName: " + getServletName() + "</br>");
        writer.write("# ServletInfo: " + getServletInfo() + "</br>");

        writer.write("<h3># ServletConfig: InitParameter</h3>");
        Enumeration<String> initParameterNames = getServletConfig().getInitParameterNames();
        while (initParameterNames.hasMoreElements()) {
            String param = initParameterNames.nextElement();
            writer.write(param + " = " + getServletConfig().getInitParameter(param) + "</br>");
        }

        writer.write("<h3># ServletContext: InitParameter</h3>");
        initParameterNames = getServletContext().getInitParameterNames();
        while (initParameterNames.hasMoreElements()) {
            String param = initParameterNames.nextElement();
            writer.write(param + " = " + getServletConfig().getInitParameter(param) + "</br>");
        }

        writer.write("\n<h2># Request</h2>");
        writer.write("\nMethod:      " + request.getMethod() + "</br>");
        writer.write("\nServletPath: " + request.getServletPath() + "</br>");
        writer.write("\nAuthType:    " + request.getAuthType() + "</br>");
        writer.write("\nSession:     " + request.getSession() + "</br>");

        writer.write("\n<h3># Headers</h3>");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            writer.write(header + " = " + request.getHeader(header) + "</br>");
        }

        writer.write("\n<h3># Params</h3>\n");
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String param = paramNames.nextElement();
            writer.write(param + " = " + request.getParameter(param) + "</br>");
        }

    }
}