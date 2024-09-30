package org.project.todayclothes.controller;

import lombok.Getter;
import org.project.todayclothes.entity.User;
import org.project.todayclothes.exception.CustomException;
import org.project.todayclothes.exception.code.UserErrorCode;
import org.project.todayclothes.util.ApiResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @GetMapping
    public List<User> getUsers() {
        return Arrays.asList(new User("John", 30), new User("Jane", 25));

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        if (id == 1L) {
            return ApiResponseUtil.createSuccessResponse("John Doe");
        } else {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }
    }
}
