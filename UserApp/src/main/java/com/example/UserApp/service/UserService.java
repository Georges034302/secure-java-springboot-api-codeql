package com.example.UserApp.service;

import com.example.UserApp.model.User;
import com.example.UserApp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public Optional<User> getUserById(Long id) {
        return repo.findById(id);
    }
}