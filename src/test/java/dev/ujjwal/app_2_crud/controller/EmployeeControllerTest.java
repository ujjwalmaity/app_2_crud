package dev.ujjwal.app_2_crud.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ujjwal.app_2_crud.dto.EmployeeDto;
import dev.ujjwal.app_2_crud.dto.EmployeeRegisterDto;
import dev.ujjwal.app_2_crud.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSaveEmployee() throws Exception {
        EmployeeDto inputEmployee = new EmployeeDto(null, "John", "Doe", "john@gmail.com", "9876543210");
        EmployeeDto savedEmployee = new EmployeeDto(1L, "John", "Doe", "john@gmail.com", "9876543210");

        when(employeeService.saveEmployee(any(EmployeeRegisterDto.class))).thenReturn(savedEmployee);

        mockMvc.perform(post("/api/v1/employee/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputEmployee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john@gmail.com"))
                .andExpect(jsonPath("$.phoneNumber").value("9876543210"));

        verify(employeeService, times(1)).saveEmployee(any(EmployeeRegisterDto.class));
    }

    @Test
    void testFindEmployee() throws Exception {
        EmployeeDto employee = new EmployeeDto(1L, "John", "Doe", "john@gmail.com", "9876543210");

        when(employeeService.findEmployee(1L)).thenReturn(employee);

        mockMvc.perform(get("/api/v1/employee/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john@gmail.com"))
                .andExpect(jsonPath("$.phoneNumber").value("9876543210"));

        verify(employeeService, times(1)).findEmployee(1L);
    }

    @Test
    void testFindAllEmployee() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(
                new EmployeeDto(1L, "John", "Doe", "john@gmail.com", "9876543210"),
                new EmployeeDto(2L, "Smith", "M", "smith@gmail.com", "0123456789")
        );

        when(employeeService.findAllEmployee()).thenReturn(employees);

        mockMvc.perform(get("/api/v1/employee/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].firstName").value("Smith"));

        verify(employeeService, times(1)).findAllEmployee();
    }

    @Test
    void testUpdateEmployee() throws Exception {
        EmployeeDto inputEmployee = new EmployeeDto(1L, "John", "Doe", "john@g.co", "123");
        EmployeeDto updatedEmployee = new EmployeeDto(1L, "John", "Doe", "john@g.co", "123");

        when(employeeService.updateEmployee(any(EmployeeDto.class))).thenReturn(updatedEmployee);

        mockMvc.perform(put("/api/v1/employee/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputEmployee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john@g.co"))
                .andExpect(jsonPath("$.phoneNumber").value("123"));

        verify(employeeService, times(1)).updateEmployee(any(EmployeeDto.class));
    }

    @Test
    void testDeleteEmployee() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/v1/employee/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Success"));

        verify(employeeService, times(1)).deleteEmployee(1L);
    }
}