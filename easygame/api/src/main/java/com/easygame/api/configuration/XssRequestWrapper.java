//package com.easygame.api.configuration;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.ReadListener;
//import jakarta.servlet.ServletInputStream;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletRequestWrapper;
//import org.apache.commons.text.StringEscapeUtils;
//import org.owasp.encoder.Encode;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//
//public class XssRequestWrapper extends HttpServletRequestWrapper {
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    public XssRequestWrapper(HttpServletRequest request) {
//        super(request);
//    }
//
//    @Override
//    public String getParameter(String name) {
//        return clean(super.getParameter(name));
//    }
//
//    @Override
//    public String[] getParameterValues(String name) {
//        String[] values = super.getParameterValues(name);
//        if (values == null) return null;
//
//        String[] cleaned = new String[values.length];
//        for (int i = 0; i < values.length; i++) {
//            cleaned[i] = clean(values[i]);
//        }
//        return cleaned;
//    }
//
//    private String clean(String value) {
//        return value == null ? null : StringEscapeUtils.escapeHtml4(value);
//    }
//
//    @Override
//    public ServletInputStream getInputStream() throws IOException {
//        ServletInputStream originalInputStream = super.getInputStream();
//        String requestBody = new String(originalInputStream.readAllBytes());
//
//        String sanitizedBody = sanitizeInput(requestBody);
//
//        return new ServletInputStream() {
//            private final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
//                    sanitizedBody.getBytes()
//            );
//
//            @Override
//            public int read() throws IOException {
//                return byteArrayInputStream.read();
//            }
//
//            @Override
//            public boolean isFinished() {
//                return byteArrayInputStream.available() == 0;
//            }
//
//            @Override
//            public boolean isReady() {
//                return true;
//            }
//
//            @Override
//            public void setReadListener(ReadListener readListener) {
//
//            }
//        };
//    }
//
//    private String sanitizeInput(String input) {
//        return Encode.forHtml(input); // Implement appropriate sanitization logic
//    }
//}
