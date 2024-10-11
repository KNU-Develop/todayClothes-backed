package org.project.todayclothes.controller;

import org.project.todayclothes.entity.User;
import org.project.todayclothes.exception.BusinessException;
import org.project.todayclothes.exception.code.UserErrorCode;
import org.project.todayclothes.util.ApiResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @GetMapping
    public List<User> getUsers() {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        if (id == 1L) {
            return ApiResponseUtil.createSuccessResponse("John Doe");
        } else {
            throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
        }
    }
}
