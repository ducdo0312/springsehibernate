package com.example.springsehibernate.Controller;

import com.example.springsehibernate.Entity.User;
import com.example.springsehibernate.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    UserService userService;

    @ModelAttribute("roles")
    public List<String> getUserRoles(Authentication authentication) {
        if (authentication == null) {
            return Collections.emptyList();
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    @ModelAttribute("realname")
    public String getCurrentUserRealname(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername());
            return user.getRealname();
        }
        return null; // Trả về null hoặc một giá trị mặc định nếu người dùng không đăng nhập
    }

    @ModelAttribute("accountId")
    public Long addAccountIdToModel(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername());

            if (user != null) {
                return user.getOwnerId();
            }
        }
        return null; // Hoặc giá trị mặc định khác tùy thuộc vào trường hợp của bạn.
    }

}

