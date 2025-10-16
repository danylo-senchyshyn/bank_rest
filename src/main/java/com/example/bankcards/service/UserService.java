package com.example.bankcards.service;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CustomException;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Найти пользователя по username
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("Пользователь не найден"));
    }

    // Создать нового пользователя (только ADMIN)
    public User createUser(String username, String password, Role role) {
        if(userRepository.findByUsername(username).isPresent()) {
            throw new CustomException("Пользователь с таким username уже существует");
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .active(true)
                .build();

        return userRepository.save(user);
    }

    // Удалить всех пользователей (для тестов)
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    // Заблокировать пользователя
    public void blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("Пользователь не найден"));
        user.setActive(false);
        userRepository.save(user);
    }

    // Разблокировать пользователя
    public void unblockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("Пользователь не найден"));
        user.setActive(true);
        userRepository.save(user);
    }

    // Получить всех пользователей (для админа)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}