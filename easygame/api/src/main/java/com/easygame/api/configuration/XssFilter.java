//package com.easygame.api.configuration;
//
//import jakarta.servlet.*;
//import jakarta.servlet.annotation.WebFilter;
//import jakarta.servlet.http.HttpServletRequest;
//
//import java.io.IOException;
//
//@WebFilter("/*")
//public class XssFilter implements Filter {
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//        chain.doFilter(new XssRequestWrapper((HttpServletRequest) request), response);
//    }
//}
