package dev.ujjwal.app_2_crud.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RequestResponseLoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        StringBuilder logText = new StringBuilder();

        // Wrap the request and response
        CustomHttpRequestWrapper wrappedRequest = new CustomHttpRequestWrapper(request);
        CustomHttpResponseWrapper wrappedResponse = new CustomHttpResponseWrapper(response);

        // Log the request
        String requestParam = getQueryParams(request);
        String requestBody = new String(wrappedRequest.getBody(), StandardCharsets.UTF_8);
        requestBody = requestBody.lines().map(String::trim).collect(Collectors.joining());
        logText.append("Request: ").append(request.getMethod()).append(" ");
        logText.append(request.getRequestURL()).append(" ");
        if (!requestParam.isEmpty()) logText.append(requestParam).append(" ");
        if (!requestBody.isEmpty()) logText.append(requestBody);
//        request.getHeaderNames().asIterator().forEachRemaining(header ->
//                logText.append("\n").append(header).append(": ").append(request.getHeader(header)));

        // Process the request
        filterChain.doFilter(wrappedRequest, wrappedResponse);

        // Log the response
        String responseBody = new String(wrappedResponse.getBody(), StandardCharsets.UTF_8);
        logText.append("\nResponse: ").append(response.getStatus()).append(" ");
        if (!responseBody.isEmpty()) logText.append(responseBody);

        log.trace(logText.toString());

        // Write the response body back to the original response
        PrintWriter writer = response.getWriter();
        writer.write(responseBody);
        writer.flush();
    }

    private String getQueryParams(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        return parameterMap.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + String.join(",", entry.getValue()))
                .collect(Collectors.joining("&"));
    }
}

class CustomHttpRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    public CustomHttpRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        try (InputStream inputStream = request.getInputStream()) {
            this.body = inputStream.readAllBytes();
        }
    }

    public byte[] getBody() {
        return this.body;
    }

    @Override
    public ServletInputStream getInputStream() {
        return new ServletInputStream() {
            private final ByteArrayInputStream buffer = new ByteArrayInputStream(body);

            @Override
            public int read() {
                return buffer.read();
            }

            @Override
            public boolean isFinished() {
                return buffer.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {
                // Not implemented
            }
        };
    }
}

class CustomHttpResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final PrintWriter writer;

    public CustomHttpResponseWrapper(HttpServletResponse response) {
        super(response);
        writer = new PrintWriter(buffer);
    }

    public byte[] getBody() {
        return buffer.toByteArray();
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return new ServletOutputStream() {
            @Override
            public void write(int b) {
                buffer.write(b);
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener listener) {
                // Not implemented
            }
        };
    }

    @Override
    public PrintWriter getWriter() {
        return writer;
    }

    @Override
    public void flushBuffer() throws IOException {
        super.flushBuffer();
        writer.flush();
    }
}