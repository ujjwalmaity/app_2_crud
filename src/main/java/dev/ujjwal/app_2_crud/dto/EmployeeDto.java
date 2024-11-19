package dev.ujjwal.app_2_crud.dto;

import lombok.Data;

@Data
public class EmployeeDto {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;
}