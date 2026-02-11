package com.udaya.service;

import com.udaya.exception.GlobalException;
import com.udaya.model.User;
import com.udaya.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new GlobalException("User not found", 404));
    }

    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }
}
