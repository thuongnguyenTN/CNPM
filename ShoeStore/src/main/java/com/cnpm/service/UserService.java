package com.cnpm.service;

import com.cnpm.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User saveUser(User user);
    User findByUsername(String username);
    List<User> findAllUsers();
    void deleteUser(Integer id);
    User saveRawUser(User user);
    Optional<User> findById(Integer id);
    void updatePassword(User user, String newPassword); 
    User saveNewUser(User user);
    
    User findByEmail(String email);
    User findByResetPasswordToken(String token);
    void createPasswordResetTokenForUser(User user, String token);
}