package com.example.springsehibernate.DTO;

import com.example.springsehibernate.Entity.RoleEnum;
import lombok.Data;

@Data
public class UserRegistrationDto {
    private String username;
    private String password;
    private String realname;
    private Long OwnerID;
    private RoleEnum role;
}
