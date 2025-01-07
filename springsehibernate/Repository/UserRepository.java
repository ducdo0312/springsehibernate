package com.example.springsehibernate.Repository;

import com.example.springsehibernate.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    boolean existsByOwnerId(Long ownerId);
}
