package com.easygame.api.controller;

import com.easygame.api.security.CustomUserDetails;
import com.easygame.api.response.ApiResponse;
import org.springframework.security.core.context.SecurityContextHolder;

public class BaseController {
    public <T> ApiResponse<T> responseSuccess(T data) {
        return new ApiResponse<>("success", 0, data);
    }

    public CustomUserDetails getAuthentication() {
        try {
            return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            return null;
        }
    }
}
