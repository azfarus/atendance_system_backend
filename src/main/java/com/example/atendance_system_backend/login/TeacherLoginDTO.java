package com.example.atendance_system_backend.login;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class TeacherLoginDTO {
    private Long id;
    private  String password;
}
