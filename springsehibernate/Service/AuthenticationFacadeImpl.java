package com.example.springsehibernate.Service;

import com.example.springsehibernate.Security.AuthenticationFacade;
import com.example.springsehibernate.Entity.User;
import com.example.springsehibernate.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationFacadeImpl implements AuthenticationFacade {

    @Autowired
    private UserRepository userRepository; // Giả sử bạn có UserRepository

    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null; // Hoặc xử lý khác nếu người dùng không được xác thực
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }
}



