package com.example.atendance_system_backend.controller;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoginDTO {
    private Long id;
    private  String password;
}
