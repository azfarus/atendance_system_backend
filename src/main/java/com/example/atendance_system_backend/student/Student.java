package com.example.atendance_system_backend.student;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    @Id
    private  Long id;

    private  String name;

    private String mail;

    private  String password;

    private Long semester;

    private  String department;

    private Long fileId;

    private  Long phoneNumber;

    private String address;

}
