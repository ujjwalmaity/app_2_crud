package dev.ujjwal.app_2_crud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRegisterDto {
    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;
}