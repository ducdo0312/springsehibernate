package com.example.springsehibernate.Entity;

import javax.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long UserID;

    @Column(nullable = false, unique = true)
    private String username;
    private String password;

    @Column(name = "OwnerID")
    private Long ownerId;

    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @Column(name = "realname")
    private String realname;
}
