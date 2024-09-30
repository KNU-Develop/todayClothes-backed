package org.project.todayclothes.controller;

import lombok.Getter;
import org.project.todayclothes.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @GetMapping
    public List<User> getUsers() {
//        return Arrays.asList(new User("John", 30), new User("Jane", 25));
        return null;
    }
}
