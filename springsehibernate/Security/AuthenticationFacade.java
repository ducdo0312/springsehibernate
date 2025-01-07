package com.example.springsehibernate.Security;

import com.example.springsehibernate.Entity.User;
import org.springframework.security.core.Authentication;

public interface AuthenticationFacade {
    Authentication getAuthentication();
    User getCurrentUser(); // Đảm bảo phương thức này được định nghĩa
}
